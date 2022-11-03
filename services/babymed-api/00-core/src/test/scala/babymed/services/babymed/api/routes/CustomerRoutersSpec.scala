package babymed.services.babymed.api.routes

import scala.concurrent.duration.DurationInt

import cats.effect.kernel.Sync
import ciris.Secret
import dev.profunktor.auth.jwt.JwtToken
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.Method.POST
import org.http4s.Request
import org.http4s.Status
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
import babymed.services.users.generators.CustomerGenerators
import babymed.services.users.generators.UserGenerators
import babymed.services.users.proto.Customers
import babymed.services.users.proto.Users
import babymed.support.redis.RedisClientMock
import babymed.support.services.syntax.all.deriveEntityDecoder
import babymed.support.services.syntax.all.deriveEntityEncoder
import babymed.support.services.syntax.all.http4SyntaxReqOps
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
  }

  val customers: Customers[F] = new Customers[F] {
    override def createCustomers(createCustomer: CreateCustomer): F[Customer] =
      Sync[F].delay(customer)
    override def getCustomers(filters: SearchFilters): F[List[CustomerWithAddress]] =
      Sync[F].delay(List(customerWithAddress))
    override def getTotalCustomers(filters: SearchFilters): F[Long] =
      Sync[F].delay(total)
    override def getCustomerById(
        customerId: types.CustomerId
      ): CustomerRoutersSpec.F[Option[CustomerWithAddress]] =
      Sync[F].delay(Option(customerWithAddress))
    override def getRegions: CustomerRoutersSpec.F[List[Region]] = Sync[F].delay(List(region))
    override def getTownsByRegionId(regionId: types.RegionId): CustomerRoutersSpec.F[List[Town]] =
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
      POST(SearchFilters.Empty, uri"/customer/report").bearer(
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
      POST(SearchFilters.Empty, uri"/customer/report/summary").bearer(
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
}
