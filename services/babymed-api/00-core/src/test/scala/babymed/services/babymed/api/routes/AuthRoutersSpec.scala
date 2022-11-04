package babymed.services.babymed.api.routes

import scala.concurrent.duration.DurationInt

import cats.effect.kernel.Sync
import cats.implicits._
import ciris.Secret
import dev.profunktor.auth.jwt.JwtToken
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.Method.GET
import org.http4s.Method.POST
import org.http4s.Status
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import tsec.passwordhashers.jca.SCrypt

import babymed.refinements.Phone
import babymed.services.auth.JwtConfig
import babymed.services.auth.domain.Credentials
import babymed.services.auth.domain.types._
import babymed.services.auth.impl.Security
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

  def users(errorType: Option[String] = None): Users[F] = new Users[F] {
    override def find(phone: Phone): F[Option[UserAndHash]] =
      errorType match {
        case Some("userNotFound") =>
          Sync[F].pure(None)
        case Some("wrongPassword") =>
          SCrypt.hashpw[F](nonEmptyStringGen(8).get).map { hash =>
            UserAndHash(user, hash).some
          }
        case None =>
          SCrypt.hashpw[F](credentials.password).map { hash =>
            UserAndHash(user.copy(phone = credentials.phone), hash).some
          }
        case _ => Sync[F].raiseError(new Exception("Error type not found"))
      }
    override def validationAndCreate(createUser: CreateUser): F[User] = ???
    override def validationAndEdit(editUser: EditUser): F[Unit] = ???
    override def get(filters: UserFilters): F[List[User]] = ???
    override def delete(userId: UserId): F[Unit] = ???
  }

  test("Authorization - Login [ OK ]") {
    val security = Security.make[F](jwtConfig, users(), RedisClientMock[F])
    val request = POST(credentials, uri"/auth/login")
    expectHttpStatus(AuthRoutes[F](security).routes, request)(Status.Ok)
  }

  test("Authorization - Incorrect login") {
    val security = Security.make[F](jwtConfig, users("userNotFound".some), RedisClientMock[F])
    val request = POST(credentials, uri"/auth/login")
    expectHttpStatus(AuthRoutes[F](security).routes, request)(Status.Forbidden)
  }

  test("Authorization - Incorrect password") {
    val security = Security.make[F](jwtConfig, users("wrongPassword".some), RedisClientMock[F])
    val request = POST(credentials, uri"/auth/login")
    expectHttpStatus(AuthRoutes[F](security).routes, request)(Status.Forbidden)
  }

  test("Authorization - Logout [ OK ]") {
    val security = Security.make[F](jwtConfig, users(), RedisClientMock[F])
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

  test("Authorization - Logout [ Unauthorized ]") {
    val security = Security.make[F](jwtConfig, users(), RedisClientMock[F])
    expectHttpStatus(AuthRoutes[F](security).routes, GET(uri"/auth/logout"))(Status.Forbidden)
      .handleError {
        fail("Test failed")
      }
  }
}
