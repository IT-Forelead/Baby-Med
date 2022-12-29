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
import babymed.domain.Role.Doctor
import babymed.domain.Role.TechAdmin
import babymed.refinements.Phone
import babymed.services.auth.JwtConfig
import babymed.services.auth.domain.Credentials
import babymed.services.auth.domain.types._
import babymed.services.auth.impl.Security
import babymed.services.users.domain._
import babymed.services.users.domain.types.UserId
import babymed.services.users.generators.UserGenerators
import babymed.services.users.proto.Users
import babymed.services.visits.domain.CheckupExpense
import babymed.services.visits.domain.CheckupExpenseFilters
import babymed.services.visits.domain.CheckupExpenseInfo
import babymed.services.visits.domain.CreateCheckupExpense
import babymed.services.visits.domain.CreateDoctorShare
import babymed.services.visits.domain.DoctorShare
import babymed.services.visits.domain.DoctorShareInfo
import babymed.services.visits.domain.types.DoctorShareId
import babymed.services.visits.generators.CheckupExpenseGenerators
import babymed.services.visits.proto.CheckupExpenses
import babymed.support.redis.RedisClientMock
import babymed.support.services.syntax.all._
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.test.HttpSuite

object CheckupExpenseRoutersSpec
    extends HttpSuite
       with CheckupExpenseGenerators
       with UserGenerators {
  val jwtConfig: JwtConfig =
    JwtConfig(
      Secret(JwtAccessTokenKey(NonEmptyString.unsafeFrom("test"))),
      TokenExpiration(1.minutes),
    )

  lazy val user: User = userGen.get
  lazy val credentials: Credentials =
    Credentials(phoneGen.get, NonEmptyString.unsafeFrom(nonEmptyStringGen(8).get))
  lazy val checkupExpenseInfo: CheckupExpenseInfo = checkupExpenseInfoGen.get
  lazy val doctorShareInfo: DoctorShareInfo = doctorShareInfoGen.get
  lazy val doctorShare: DoctorShare = doctorShareGen.get
  lazy val checkupExpense: CheckupExpense = checkupExpenseGen.get
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

  val checkupExpenses: CheckupExpenses[F] = new CheckupExpenses[F] {
    override def createDoctorShare(createData: CreateDoctorShare): F[DoctorShare] =
      Sync[F].delay(doctorShare)
    override def get(
        filters: CheckupExpenseFilters
      ): F[ResponseData[CheckupExpenseInfo]] =
      Sync[F].delay(ResponseData(List(checkupExpenseInfo), total))
    override def getTotal(filters: CheckupExpenseFilters): F[Long] =
      Sync[F].delay(total)
    override def getDoctorShares: F[List[DoctorShareInfo]] =
      Sync[F].delay(List(doctorShareInfo))
    override def deleteDoctorShare(id: DoctorShareId): F[Unit] =
      Sync[F].unit
    override def create(
        createCheckupExpenses: List[
          CreateCheckupExpense
        ]
      ): CheckupExpenseRoutersSpec.F[List[CheckupExpense]] =
      Sync[F].delay(List(checkupExpense))
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

  test("Create doctor share with incorrect role") {
    authedReq(Doctor) { token =>
      POST(createDoctorShareGen().get, uri"/checkup-expense/create/doctor-share")
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectNotFound(CheckupExpenseRouters[F](security, checkupExpenses).routes, request)
    }
  }

  test("Create doctor share with correct role") {
    authedReq() { token =>
      POST(createDoctorShareGen().get, uri"/checkup-expense/create/doctor-share")
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpStatus(
          CheckupExpenseRouters[F](security, checkupExpenses).routes,
          request,
        )(Status.NoContent)
    }
  }

  test("Get Checkup Expenses") {
    authedReq() { token =>
      POST(CheckupExpenseFilters.Empty, uri"/checkup-expense/report")
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpBodyAndStatus(
          CheckupExpenseRouters[F](security, checkupExpenses).routes,
          request,
        )(
          ResponseData(List(checkupExpenseInfo), total),
          Status.Ok,
        )
    }
  }

  test("Get Checkup Expenses Total") {
    authedReq() { token =>
      POST(CheckupExpenseFilters.Empty, uri"/checkup-expense/report/summary")
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpBodyAndStatus(
          CheckupExpenseRouters[F](security, checkupExpenses).routes,
          request,
        )(total, Status.Ok)
    }
  }

  test("Get All Doctor Shares") {
    authedReq() { token =>
      GET(uri"/checkup-expense/doctor-shares".withQueryParam("x-token", token.value))
    } {
      case request -> security =>
        expectHttpBodyAndStatus(
          CheckupExpenseRouters[F](security, checkupExpenses).routes,
          request,
        )(List(doctorShareInfo), Status.Ok)
    }
  }

  test("Delete Doctor Share with incorrect role") {
    authedReq(Doctor) { token =>
      val doctorShareId: DoctorShareId = doctorShareIdGen.get
      GET(Uri.unsafeFromString(s"/checkup-expense/delete-doctor-share/$doctorShareId")).bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectNotFound(CheckupExpenseRouters[F](security, checkupExpenses).routes, request)
    }
  }

  test("Delete Doctor Share with correct role") {
    authedReq() { token =>
      val doctorShareId: DoctorShareId = doctorShareIdGen.get
      GET(Uri.unsafeFromString(s"/checkup-expense/delete-doctor-share/$doctorShareId"))
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpStatus(
          CheckupExpenseRouters[F](security, checkupExpenses).routes,
          request,
        )(Status.NoContent)
    }
  }
}
