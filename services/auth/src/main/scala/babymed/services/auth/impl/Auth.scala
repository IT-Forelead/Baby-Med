package babymed.services.auth.impl

import scala.concurrent.duration.DurationInt

import babymed.exception.AuthError
import babymed.exception.AuthError.NoSuchUser
import babymed.exception.AuthError.PasswordDoesNotMatch
import babymed.services.auth.domain.Credentials
import babymed.services.auth.domain.types.UserJwtAuth
import babymed.services.auth.utils.AuthMiddleware
import babymed.services.auth.utils.AuthMiddleware.AuthData
import babymed.services.auth.utils.Tokens
import babymed.services.users.domain.User
import babymed.services.users.proto.Users
import babymed.support.redis.RedisClient
import babymed.syntax.all.circeSyntaxDecoderOps
import cats.conversions.all.autoWidenFunctor
import cats.data.OptionT
import cats.effect.Sync
import cats.syntax.all._
import dev.profunktor.auth.AuthHeaders
import dev.profunktor.auth.jwt.JwtToken
import eu.timepit.refined.auto.autoUnwrap
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.Request
import org.http4s.server
import tsec.passwordhashers.jca.SCrypt

trait Auth[F[_]] {
  def login(credentials: Credentials): F[JwtToken]
  def destroySession(request: Request[F], login: NonEmptyString): F[Unit]
  def usersMiddleware(userJwtAuth: UserJwtAuth): server.AuthMiddleware[F, User]
}

object Auth {
  def apply[F[_]: Sync](
      tokens: Tokens[F],
      users: Users[F],
      redis: RedisClient[F],
    ): Auth[F] =
    new Auth[F] {
      private val TokenExpiration = 8.hours

      override def login(credentials: Credentials): F[JwtToken] =
        users.find(credentials.phone).flatMap {
          case None =>
            NoSuchUser("User Not Found").raiseError[F, JwtToken]
          case Some(userAndHash)
               if !SCrypt.checkpwUnsafe(credentials.password, userAndHash.password) =>
            PasswordDoesNotMatch("Password does not match").raiseError[F, JwtToken]
          case Some(userAndHash) =>
            OptionT(redis.get(credentials.phone)).cataF(
              tokens.create.flatTap { t =>
                redis.put(t.value, userAndHash.user, TokenExpiration) >>
                  redis.put(credentials.phone, t.value, TokenExpiration)
              },
              token => JwtToken(token).pure[F],
            )
        }

      override def destroySession(request: Request[F], login: NonEmptyString): F[Unit] =
        AuthHeaders
          .getBearerToken(request)
          .traverse_(token => redis.del(token.value, login))

      override def usersMiddleware(userJwtAuth: UserJwtAuth): server.AuthMiddleware[F, User] = {
        def findUser(token: JwtToken): F[Option[User]] =
          OptionT(redis.get(token.value))
            .map(_.as[User])
            .value

        def prolongSession(auth: AuthData): F[Option[JwtToken]] =
          OptionT(redis.get(auth.token.value))
            .map(_.as[User])
            .flatMap { user =>
              OptionT(tokens.validateAndUpdate(auth.claim))
                .semiflatTap(newToken =>
                  redis.del(auth.token.value) >>
                    redis.put(newToken.value, user, TokenExpiration) >>
                    redis.put(user.phone.value, newToken.value, TokenExpiration)
                )
            }
            .value

        def destroySession(token: JwtToken): F[Unit] =
          OptionT(findUser(token))
            .semiflatMap(user => redis.del(token.value, user.phone.value))
            .value
            .void
        AuthMiddleware[F, User](
          userJwtAuth.value,
          findUser,
          prolongSession,
          destroySession,
        )
      }
    }
}
