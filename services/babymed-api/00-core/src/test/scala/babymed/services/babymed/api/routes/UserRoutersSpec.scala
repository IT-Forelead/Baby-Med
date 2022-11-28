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
import babymed.domain.Role.Admin
import babymed.domain.Role.SuperManager
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
import babymed.support.redis.RedisClientMock
import babymed.support.services.syntax.all.deriveEntityDecoder
import babymed.support.services.syntax.all.deriveEntityEncoder
import babymed.support.services.syntax.all.http4SyntaxReqOps
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.test.HttpSuite

object UserRoutersSpec extends HttpSuite with UserGenerators {
  val jwtConfig: JwtConfig =
    JwtConfig(
      Secret(JwtAccessTokenKey(NonEmptyString.unsafeFrom("test"))),
      TokenExpiration(1.minutes),
    )

  lazy val total: Long = Gen.long.get
  lazy val user: User = userGen.get
  lazy val editUser: EditUser = editUserGen().get
  lazy val createUser: CreateUser = createUserGen().get
  lazy val credentials: Credentials =
    Credentials(phoneGen.get, NonEmptyString.unsafeFrom(nonEmptyStringGen(8).get))

  def users(role: Role = Admin): Users[F] = new Users[F] {
    override def find(phone: Phone): F[Option[UserAndHash]] =
      Sync[F].pure(
        Option(UserAndHash(user.copy(role = role), SCrypt.hashpwUnsafe(credentials.password)))
      )
    override def validationAndCreate(createUser: CreateUser): F[User] = Sync[F].delay(user)
    override def validationAndEdit(editUser: EditUser): F[Unit] = Sync[F].unit
    override def get(filters: UserFilters): F[ResponseData[User]] =
      Sync[F].delay(ResponseData(List(user), total))
    override def delete(userId: UserId): F[Unit] = Sync[F].unit
    override def getTotal(filters: UserFilters): F[Long] = Sync[F].delay(total)
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

  test("Create user with incorrect role") {
    authedReq(TechAdmin) { token =>
      POST(createUser, uri"/user").bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectNotFound(UserRouters[F](security, users()).routes, request)
    }
  }

  test("Create user with correct role") {
    authedReq(SuperManager) { token =>
      POST(createUser, uri"/user").bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpStatus(UserRouters[F](security, users()).routes, request)(Status.NoContent)
    }
  }

  test("Get users with incorrect role") {
    authedReq() { token =>
      POST(UserFilters.Empty, uri"/user/report").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectNotFound(UserRouters[F](security, users()).routes, request)
    }
  }

  test("Get users with correct role") {
    authedReq(SuperManager) { token =>
      POST(UserFilters.Empty, uri"/user/report").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectHttpBodyAndStatus(UserRouters[F](security, users()).routes, request)(
          ResponseData(List(user), total),
          Status.Ok,
        )
    }
  }

  test("Update user with correct role") {
    authedReq(SuperManager) { token =>
      POST(editUser, uri"/user/update").bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpStatus(UserRouters[F](security, users()).routes, request)(Status.NoContent)
    }
  }

  test("Delete user") {
    authedReq(SuperManager) { token =>
      GET(
        Uri
          .unsafeFromString(s"/user/delete/${userIdGen.get}")
          .withQueryParam("x-token", token.value)
      )
    } {
      case request -> security =>
        expectHttpStatus(UserRouters[F](security, users()).routes, request)(Status.NoContent)
    }
  }
}
