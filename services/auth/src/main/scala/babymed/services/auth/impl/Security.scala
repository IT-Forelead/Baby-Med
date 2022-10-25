package babymed.services.auth.impl

import babymed.services.auth.JwtConfig
import babymed.services.auth.domain.types.UserJwtAuth
import babymed.services.auth.utils.JwtExpire
import babymed.services.auth.utils.Tokens
import babymed.services.users.proto.Users
import babymed.support.redis.RedisClient
import cats.effect._
import dev.profunktor.auth.jwt._
import eu.timepit.refined.auto._
import pdi.jwt._

object Security {
  def make[F[_]: Sync](
      config: JwtConfig,
      users: Users[F],
      redis: RedisClient[F],
    ): Security[F] = {
    val tokens =
      Tokens.make[F](JwtExpire[F], config.tokenKey.value, config.tokenExpiration)
    new Security[F](
      Auth[F](tokens, users, redis),
      UserJwtAuth(
        JwtAuth.hmac(config.tokenKey.value.secret, JwtAlgorithm.HS256)
      ),
    )
  }
}

final class Security[F[_]] private (
    val auth: Auth[F],
    val userJwtAuth: UserJwtAuth,
  )
