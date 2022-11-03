package babymed.services.babymed.api.routes

import scala.concurrent.duration.DurationInt

import babymed.refinements.Phone
import babymed.services.auth.JwtConfig
import babymed.services.auth.domain.Credentials
import babymed.services.auth.domain.types._
import babymed.services.auth.impl.Security
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.generators.UserGenerators
import babymed.services.users.proto.Users
import babymed.support.redis.RedisClientMock
import babymed.support.services.syntax.all._
import babymed.test.HttpSuite
import cats.effect.Sync
import ciris.Secret
import dev.profunktor.auth.jwt.JwtToken
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.Method.GET
import org.http4s.Method.POST
import org.http4s.Status
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax

object AuthRoutersSpec extends HttpSuite with UserGenerators {
  val jwtConfig: JwtConfig =
    JwtConfig(
      Secret(JwtAccessTokenKey(NonEmptyString.unsafeFrom("test"))),
      TokenExpiration(1.minutes),
    )
  lazy val user: User = userGen.sample.get
  lazy val userAndHash: UserAndHash = userAndHashGen.get
  lazy val credentials: Credentials =
    Credentials(phoneGen.get, NonEmptyString.unsafeFrom(nonEmptyStringGen(8).get))

  def users(isCorrectLogin: Boolean): Users[F] = new Users[F] {
    override def find(phone: Phone): F[Option[UserAndHash]] =
      if (isCorrectLogin)
        Sync[F].pure(Option(userAndHash))
      else
        Sync[F].pure(None)
    override def create(createUser: CreateUser): AuthRoutersSpec.F[User] = ???
  }

  test("Authorization - Login [ OK ]") {
    val security = Security.make[F](jwtConfig, users(true), RedisClientMock[F])
    val request = POST(credentials, uri"/auth/login")
    expectHttpStatus(AuthRoutes[F](security).routes, request)(Status.Ok)
  }

  test("Authorization - Incorrect login") {
    val security = Security.make[F](jwtConfig, users(false), RedisClientMock[F])
    val request = POST(credentials, uri"/auth/login")
    expectHttpStatus(AuthRoutes[F](security).routes, request)(Status.Forbidden)
  }

  test("Authorization - Incorrect password") {
    val security = Security.make[F](jwtConfig, users(true), RedisClientMock[F])
    val request = POST(credentials, uri"/auth/login")
    expectHttpStatus(AuthRoutes[F](security).routes, request)(Status.Forbidden)
  }

  test("Logout - Success") {
    val security = Security.make[F](jwtConfig, users(true), RedisClientMock[F])
    val routes = AuthRoutes[F](security).routes
    routes
      .run(POST(credentials, uri"/auth/login"))
      .semiflatMap(_.as[JwtToken])
      .semiflatMap { token =>
        expectHttpStatus(
          routes,
          GET(uri"/auth/logout").bearer(NonEmptyString.unsafeFrom(token.value)),
        )(Status.NoContent)
      }
      .getOrElse(failure("Should return jwt token"))
      .handleError {
        fail("Test failed")
      }

  }

  test("Logout - Unauthorized") {
    val security = Security.make[F](jwtConfig, users(true), RedisClientMock[F])
    expectHttpStatus(AuthRoutes[F](security).routes, GET(uri"/auth/logout"))(Status.Forbidden)
      .handleError {
        fail("Test failed")
      }
  }
}
