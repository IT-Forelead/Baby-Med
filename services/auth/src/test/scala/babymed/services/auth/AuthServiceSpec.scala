package babymed.services.auth

import scala.concurrent.duration.DurationInt

import cats.effect.kernel.Sync
import cats.implicits._
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.AuthScheme
import org.http4s.Credentials.Token
import org.http4s.Request
import org.http4s.headers.Authorization
import org.http4s.implicits.http4sLiteralsSyntax
import tsec.passwordhashers.jca.SCrypt

import babymed.exception.AuthError
import babymed.refinements.Password
import babymed.refinements.Phone
import babymed.services.auth.domain.Credentials
import babymed.services.auth.domain.types.JwtAccessTokenKey
import babymed.services.auth.domain.types.TokenExpiration
import babymed.services.auth.impl.Auth
import babymed.services.auth.utils.Generators.credentialsGen
import babymed.services.auth.utils.JwtExpire
import babymed.services.auth.utils.Tokens
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.domain.UserFilters
import babymed.services.users.domain.types.UserId
import babymed.services.users.generators.UserGenerators
import babymed.services.users.proto.Users
import babymed.support.redis.RedisClientMock
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.test.HttpSuite
import babymed.test.generators.CommonGenerators

object AuthServiceSpec extends HttpSuite with CommonGenerators with UserGenerators {
  lazy val tokens: Tokens[F] = Tokens
    .make[F](
      JwtExpire[F],
      JwtAccessTokenKey(NonEmptyString.unsafeFrom("secret")),
      TokenExpiration(1.minute),
    )
  lazy val user: User = userGen.sample.get
  lazy val password: Password = passwordGen.sample.get
  lazy val credentials: Credentials = credentialsGen.sample.get

  def users(
      user: User,
      pass: Password,
      errorType: Option[String] = None,
    ): Users[F] = new Users[F] {
    override def find(phone: Phone): F[Option[UserAndHash]] =
      errorType match {
        case Some("userNotFound") =>
          Sync[F].pure(None)
        case Some("wrongPassword") =>
          SCrypt.hashpw[F](pass).map { hash =>
            UserAndHash(user, hash).some
          }
        case None =>
          SCrypt.hashpw[F](credentials.password).map { hash =>
            UserAndHash(user.copy(phone = credentials.phone), hash).some
          }
        case _ => Sync[F].raiseError(new Exception("Error type not found"))
      }
    override def validationAndCreate(createUser: CreateUser): F[User] = ???
    override def get(filters: UserFilters): F[List[User]] = ???
    override def delete(userId: UserId): F[Unit] = ???
  }

  test("Login - success") {
    Auth[F](tokens, users(user, password), RedisClientMock[F])
      .login(credentials)
      .map { token =>
        assert(token.value.nonEmpty)
      }
      .handleError(error => failure(error.toString))
  }

  test("Login - incorrect login") {
    Auth[F](tokens, users(user, password, "userNotFound".some), RedisClientMock[F])
      .login(credentials)
      .map(_ => failure("Should return NoSuchUser error"))
      .recover {
        case _: AuthError.NoSuchUser => success
        case error => failure(s"Return $error should be NoSuchUser")
      }
  }

  test("Login - incorrect password") {
    Auth[F](tokens, users(user, password, "wrongPassword".some), RedisClientMock[F])
      .login(credentials)
      .map(_ => failure("Should return PasswordDoesNotMatch error"))
      .recover {
        case _: AuthError.PasswordDoesNotMatch => success
        case error => failure(s"Return $error should be PasswordDoesNotMatch")
      }
  }

  test("Destroy session") {
    val redis = RedisClientMock[F]
    (for {
      _ <- redis.put("token", "user", 1.minute)
      _ <- redis.put(user.phone, "token", 1.minute)
      tokenBeforeDestroy <- redis.get(user.phone)
      request = Request[F](uri = uri"/test")
        .withHeaders(Authorization(Token(AuthScheme.Bearer, "token")))
      _ <- Auth[F](tokens, users(user, password), redis).destroySession(request, user.phone)
      tokenAfterDestroy <- redis.get(user.phone)
    } yield assert.all(tokenBeforeDestroy.contains("token"), tokenAfterDestroy.isEmpty))
      .handleError { error =>
        failure(error.toString)
      }
  }
}
