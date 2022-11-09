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

import babymed.domain.Role
import babymed.domain.Role.Doctor
import babymed.domain.Role.TechAdmin
import babymed.refinements.Phone
import babymed.services.auth.JwtConfig
import babymed.services.auth.domain.Credentials
import babymed.services.auth.domain.types._
import babymed.services.auth.impl.Security
import babymed.services.users.domain._
import babymed.services.users.domain.types.CustomerId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.UserId
import babymed.services.users.generators.CustomerGenerators
import babymed.services.users.generators.UserGenerators
import babymed.services.users.proto.Customers
import babymed.services.users.proto.Users
import babymed.support.redis.RedisClientMock
import babymed.support.services.syntax.all._
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.test.HttpSuite

object CustomerRoutersSpec extends HttpSuite with CustomerGenerators with UserGenerators {
  val jwtConfig: JwtConfig =
    JwtConfig(
      Secret(JwtAccessTokenKey(NonEmptyString.unsafeFrom("test"))),
      TokenExpiration(1.minutes),
    )

  lazy val user: User = userGen.get
  lazy val credentials: Credentials =
    Credentials(phoneGen.get, NonEmptyString.unsafeFrom(nonEmptyStringGen(8).get))
  lazy val customer: Customer = customerGen.get
  lazy val customerWithAddress: CustomerWithAddress = customerWithAddressGen.get
  lazy val region: Region = regionGen.get
  lazy val town: Town = townGen.get
  lazy val total: Long = Gen.long.get

  def users(role: Role): Users[F] = new Users[F] {
    override def find(phone: Phone): F[Option[UserAndHash]] =
      Sync[F].pure(
        Option(UserAndHash(user.copy(role = role), SCrypt.hashpwUnsafe(credentials.password)))
      )
    override def validationAndCreate(createUser: CreateUser): F[User] = Sync[F].delay(user)
    override def validationAndEdit(editUser: EditUser): F[Unit] = ???
    override def get(filters: UserFilters): F[List[User]] = ???
    override def delete(userId: UserId): F[Unit] = ???
    override def getTotal(
        filters: UserFilters
      ): CustomerRoutersSpec.F[Long] = ???
  }

  val customers: Customers[F] = new Customers[F] {
    override def createCustomers(createCustomer: CreateCustomer): F[Customer] =
      Sync[F].delay(customer)
    override def getCustomers(filters: CustomerFilters): F[List[CustomerWithAddress]] =
      Sync[F].delay(List(customerWithAddress))
    override def getTotalCustomers(filters: CustomerFilters): F[Long] =
      Sync[F].delay(total)
    override def getCustomerById(customerId: CustomerId): F[Option[CustomerWithAddress]] =
      Sync[F].delay(Option(customerWithAddress))
    override def getRegions: F[List[Region]] = Sync[F].delay(List(region))
    override def getTownsByRegionId(regionId: RegionId): F[List[Town]] =
      Sync[F].delay(List(town))
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

  test("Create customer with incorrect role") {
    authedReq(Doctor) { token =>
      POST(createCustomerGen.get, uri"/customer").bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectNotFound(CustomerRouters[F](security, customers).routes, request)
    }
  }

  test("Create customer with correct role") {
    authedReq() { token =>
      POST(createCustomerGen.get, uri"/customer").bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpStatus(CustomerRouters[F](security, customers).routes, request)(Status.NoContent)
    }
  }

  test("Get customers") {
    authedReq() { token =>
      POST(CustomerFilters.Empty, uri"/customer/report").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectHttpBodyAndStatus(CustomerRouters[F](security, customers).routes, request)(
          List(customerWithAddress),
          Status.Ok,
        )
    }
  }

  test("Get customers total") {
    authedReq() { token =>
      POST(CustomerFilters.Empty, uri"/customer/report/summary").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectHttpBodyAndStatus(CustomerRouters[F](security, customers).routes, request)(
          total,
          Status.Ok,
        )
    }
  }

  test("Get All Regions") {
    authedReq() { token =>
      GET(
        Uri
          .unsafeFromString("/customer/regions")
          .withQueryParam("x-token", token.value)
      )
    } {
      case request -> security =>
        expectHttpBodyAndStatus(CustomerRouters[F](security, customers).routes, request)(
          List(region),
          Status.Ok,
        )
    }
  }

  test("Get Towns by RegionId") {
    authedReq() { token =>
      GET(
        Uri
          .unsafeFromString(s"/customer/towns/${regionIdGen.get}")
          .withQueryParam("x-token", token.value)
      )
    } {
      case request -> security =>
        expectHttpBodyAndStatus(CustomerRouters[F](security, customers).routes, request)(
          List(town),
          Status.Ok,
        )
    }
  }
}
