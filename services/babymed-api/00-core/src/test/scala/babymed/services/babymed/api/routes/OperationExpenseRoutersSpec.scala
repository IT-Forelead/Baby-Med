package babymed.services.babymed.api.routes

import scala.concurrent.duration.DurationInt

import cats.effect.kernel.Sync
import ciris.Secret
import dev.profunktor.auth.jwt.JwtToken
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.Method.GET
import org.http4s.Method.POST
import org.http4s.Request
import org.http4s.Status
import org.http4s.Uri
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import org.scalacheck.Gen
import tsec.passwordhashers.jca.SCrypt
import weaver.Expectations

import babymed.domain.ResponseData
import babymed.domain.Role
import babymed.domain.Role._
import babymed.refinements.Phone
import babymed.services.auth.JwtConfig
import babymed.services.auth.domain.Credentials
import babymed.services.auth.domain.types._
import babymed.services.auth.impl.Security
import babymed.services.users.domain._
import babymed.services.users.domain.types.UserId
import babymed.services.users.generators.UserGenerators
import babymed.services.users.proto.Users
import babymed.services.visits.domain.CreateOperationExpense
import babymed.services.visits.domain.OperationExpense
import babymed.services.visits.domain.OperationExpenseFilters
import babymed.services.visits.domain.OperationExpenseInfo
import babymed.services.visits.domain.OperationExpenseItemWithUser
import babymed.services.visits.domain.OperationFilters
import babymed.services.visits.domain.OperationInfo
import babymed.services.visits.domain.OperationService
import babymed.services.visits.domain.OperationServiceInfo
import babymed.services.visits.domain.types.OperationExpenseId
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.generators.OperationExpenseGenerators
import babymed.services.visits.proto.OperationExpenses
import babymed.support.redis.RedisClientMock
import babymed.support.services.syntax.all._
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.test.HttpSuite

object OperationExpenseRoutersSpec
    extends HttpSuite
       with OperationExpenseGenerators
       with UserGenerators {
  val jwtConfig: JwtConfig =
    JwtConfig(
      Secret(JwtAccessTokenKey(NonEmptyString.unsafeFrom("test"))),
      TokenExpiration(1.minutes),
    )

  lazy val user: User = userGen.get
  lazy val credentials: Credentials =
    Credentials(phoneGen.get, NonEmptyString.unsafeFrom(nonEmptyStringGen(8).get))
  lazy val operationExpense: OperationExpense = operationExpenseGen.get
  lazy val operationService: OperationService = operationServiceGen.get
  lazy val operationServiceInfo: OperationServiceInfo = operationServiceInfoGen.get
  lazy val operationInfo: OperationInfo = operationInfoGen.get
  lazy val operationExpenseWithPatientVisit: OperationExpenseInfo =
    operationExpenseWithPatientVisitGen.get
  lazy val operationExpenseItemWithUser: OperationExpenseItemWithUser =
    operationExpenseItemWithUserGen.get
  lazy val total: Long = Gen.long.get

  def users(role: Role): Users[F] = new Users[F] {
    override def find(phone: Phone): F[Option[UserAndHash]] =
      Sync[F].pure(
        Option(UserAndHash(user.copy(role = role), SCrypt.hashpwUnsafe(credentials.password)))
      )
    override def validationAndCreate(createUser: CreateUser): F[User] = Sync[F].delay(user)
    override def validationAndEdit(editUser: EditUser): F[Unit] = ???
    override def get(filters: UserFilters): F[ResponseData[User]] = ???
    override def delete(userId: UserId): F[Unit] = ???
    override def getTotal(filters: UserFilters): F[Long] = ???
    override def getSubRoles: F[List[SubRole]] = ???
  }

  val operationExpenses: OperationExpenses[F] = new OperationExpenses[F] {
    override def create(createOperationExpense: CreateOperationExpense): F[OperationExpense] =
      Sync[F].delay(operationExpense)
    override def get(
        filters: OperationExpenseFilters
      ): F[ResponseData[OperationExpenseInfo]] =
      Sync[F].delay(ResponseData(List(operationExpenseWithPatientVisit), total))
    override def getTotal(filters: OperationExpenseFilters): F[Long] =
      Sync[F].delay(total)
    override def getItemsById(id: OperationExpenseId): F[List[OperationExpenseItemWithUser]] =
      Sync[F].delay(List(operationExpenseItemWithUser))
    override def createOperationServices(serviceId: ServiceId): F[OperationService] =
      Sync[F].delay(operationService)
    override def getOperations(filters: OperationFilters): F[ResponseData[OperationInfo]] =
      Sync[F].delay(ResponseData(List(operationInfo), total))
    override def getOperationServices: F[List[OperationServiceInfo]] =
      Sync[F].delay(List(operationServiceInfo))
  }

  def authedReq(
      role: Role = TechAdmin
    )(
      request: JwtToken => Request[F]
    )(
      expect: (Request[F], Security[F]) => F[Expectations]
    ): F[Expectations] = {
    val security = Security.make[F](jwtConfig, users(role), RedisClientMock[F])
    val loginReq = POST(credentials, uri"/auth/login")
    AuthRoutes[F](security)
      .routes
      .run(loginReq)
      .semiflatMap(_.as[JwtToken])
      .cataF(
        Sync[F].delay(failure("Should return jwt token")),
        token => expect(request(token), security),
      )
      .handleError {
        fail("Test failed")
      }
  }

  test("Create patient visit with incorrect role") {
    authedReq(Doctor) { token =>
      POST(createOperationExpenseGen().get, uri"/operation-expense/create")
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectNotFound(OperationExpenseRouters[F](security, operationExpenses).routes, request)
    }
  }

  test("Create Operation Expense with correct role") {
    authedReq() { token =>
      POST(createOperationExpenseGen().get, uri"/operation-expense/create")
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpStatus(
          OperationExpenseRouters[F](security, operationExpenses).routes,
          request,
        )(Status.NoContent)
    }
  }

  test("Get Operation Expenses") {
    authedReq() { token =>
      POST(OperationExpenseFilters.Empty, uri"/operation-expense/report")
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpBodyAndStatus(
          OperationExpenseRouters[F](security, operationExpenses).routes,
          request,
        )(
          ResponseData(List(operationExpenseWithPatientVisit), total),
          Status.Ok,
        )
    }
  }

  test("Get Operation Expenses Total") {
    authedReq() { token =>
      POST(OperationExpenseFilters.Empty, uri"/operation-expense/report/summary")
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpBodyAndStatus(
          OperationExpenseRouters[F](security, operationExpenses).routes,
          request,
        )(total, Status.Ok)
    }
  }

  test("Get OperationExpense Items by Id") {
    authedReq() { token =>
      GET(
        Uri
          .unsafeFromString(s"/operation-expense/items/${regionIdGen.get}")
          .withQueryParam("x-token", token.value)
      )
    } {
      case request -> security =>
        expectHttpBodyAndStatus(
          OperationExpenseRouters[F](security, operationExpenses).routes,
          request,
        )(List(operationExpenseItemWithUser), Status.Ok)
    }
  }

  test("Create Operation Service with incorrect role") {
    authedReq(Doctor) { token =>
      POST(serviceIdGen.get, uri"/operation-expense/create/operation-service")
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectNotFound(OperationExpenseRouters[F](security, operationExpenses).routes, request)
    }
  }

  test("Create Operation Service with correct role") {
    authedReq() { token =>
      POST(serviceIdGen.get, uri"/operation-expense/create/operation-service")
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpBodyAndStatus(
          OperationExpenseRouters[F](security, operationExpenses).routes,
          request,
        )(operationService, Status.Ok)
    }
  }

  test("Get Operations") {
    authedReq() { token =>
      POST(OperationFilters.Empty, uri"/operation-expense/operations")
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpBodyAndStatus(
          OperationExpenseRouters[F](security, operationExpenses).routes,
          request,
        )(
          ResponseData(List(operationInfo), total),
          Status.Ok,
        )
    }
  }

  test("Get All Operation Services") {
    authedReq() { token =>
      GET(uri"/operation-expense/operation-services")
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpBodyAndStatus(
          OperationExpenseRouters[F](security, operationExpenses).routes,
          request,
        )(
          List(operationServiceInfo),
          Status.Ok,
        )
    }
  }
}
