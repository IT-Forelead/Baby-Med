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
import babymed.domain.Role.SuperManager
import babymed.domain.Role.TechAdmin
import babymed.refinements.Phone
import babymed.services.auth.JwtConfig
import babymed.services.auth.domain.Credentials
import babymed.services.auth.domain.types.JwtAccessTokenKey
import babymed.services.auth.domain.types.TokenExpiration
import babymed.services.auth.domain.types.tokenCodec
import babymed.services.auth.impl.Security
import babymed.services.payments.domain._
import babymed.services.payments.domain.types.PaymentId
import babymed.services.payments.generators.PaymentGenerator
import babymed.services.payments.proto.Payments
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.EditUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.domain.UserFilters
import babymed.services.users.domain.types.UserId
import babymed.services.users.generators.UserGenerators
import babymed.services.users.proto.Users
import babymed.support.redis.RedisClientMock
import babymed.support.services.syntax.all._
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.test.HttpSuite

object PaymentRoutersSpec extends HttpSuite with PaymentGenerator with UserGenerators {
  val jwtConfig: JwtConfig =
    JwtConfig(
      Secret(JwtAccessTokenKey(NonEmptyString.unsafeFrom("test"))),
      TokenExpiration(1.minutes),
    )

  lazy val user: User = User(
    id = userIdGen.get,
    createdAt = localDateTimeGen.get,
    firstname = firstNameGen.get,
    lastname = lastNameGen.get,
    role = roleGen.get,
    phone = phoneGen.get,
  )
  lazy val credentials: Credentials =
    Credentials(phoneGen.get, NonEmptyString.unsafeFrom(nonEmptyStringGen(8).get))
  lazy val payment: Payment = paymentGen.get
  lazy val createPayment: CreatePayment = createPaymentGen.get
  lazy val paymentWithCustomer: PaymentWithCustomer = paymentWithCustomerGen.get
  lazy val total: Long = Gen.long.get

  def users(role: Role): Users[F] = new Users[F] {
    override def find(phone: Phone): F[Option[UserAndHash]] =
      Sync[F].pure(
        Option(UserAndHash(user.copy(role = role), SCrypt.hashpwUnsafe(credentials.password)))
      )
    override def validationAndCreate(createUser: CreateUser): F[User] = ???
    override def validationAndEdit(editUser: EditUser): F[Unit] = ???
    override def get(filters: UserFilters): F[List[User]] = ???
    override def delete(userId: UserId): F[Unit] = ???
  }

  val payments: Payments[F] = new Payments[F] {
    override def create(createPayment: CreatePayment): F[Payment] =
      Sync[F].delay(payment)
    override def get(filters: PaymentFilters): F[List[PaymentWithCustomer]] =
      Sync[F].delay(List(paymentWithCustomer))
    override def getPaymentsTotal(filters: PaymentFilters): F[Long] =
      Sync[F].delay(total)
    override def delete(paymentId: PaymentId): F[Unit] = Sync[F].unit
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

  test("Create payment with incorrect role") {
    authedReq(Doctor) { token =>
      POST(createPayment, uri"/payment").bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectNotFound(PaymentRouters[F](security, payments).routes, request)
    }
  }

  test("Create payment with correct role") {
    authedReq() { token =>
      POST(createPayment, uri"/payment").bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpStatus(PaymentRouters[F](security, payments).routes, request)(Status.NoContent)
    }
  }

  test("Delete payment with correct role") {
    authedReq(SuperManager) { token =>
      GET(
        Uri
          .unsafeFromString(s"/payment/delete/${paymentIdGen.get}")
          .withQueryParam("x-token", token.value)
      )
    } {
      case request -> security =>
        expectHttpStatus(PaymentRouters[F](security, payments).routes, request)(Status.NoContent)
    }
  }

  test("Get payments") {
    authedReq() { token =>
      POST(PaymentFilters.Empty, uri"/payment/report").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectHttpBodyAndStatus(PaymentRouters[F](security, payments).routes, request)(
          List(paymentWithCustomer),
          Status.Ok,
        )
    }
  }

  test("Get payments :: Result Not Found") {
    authedReq() { token =>
      POST(PaymentFilters.Empty, uri"/payment/report&page=-1&limit=-30").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectNotFound(PaymentRouters[F](security, payments).routes, request)
    }
  }

  test("Get payments total") {
    authedReq() { token =>
      POST(PaymentFilters.Empty, uri"/payment/report/summary").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectHttpBodyAndStatus(PaymentRouters[F](security, payments).routes, request)(
          total,
          Status.Ok,
        )
    }
  }
}
