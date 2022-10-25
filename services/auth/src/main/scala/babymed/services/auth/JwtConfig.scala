package babymed.services.auth

import scala.concurrent.duration.FiniteDuration

import babymed.services.auth.domain.types.JwtAccessTokenKey
import babymed.services.auth.domain.types.TokenExpiration
import babymed.syntax.all.circeConfigDecoder
import cats.implicits._
import ciris._
import io.circe.generic.auto.exportDecoder

case class JwtConfig(
    tokenKey: Secret[JwtAccessTokenKey],
    tokenExpiration: TokenExpiration,
  )

object JwtConfig {
  def configValues: ConfigValue[Effect, JwtConfig] = (
    env("ACCESS_TOKEN_SECRET_KEY").as[JwtAccessTokenKey].secret,
    env("JWT_TOKEN_EXPIRATION").as[FiniteDuration].map(TokenExpiration.apply),
  ).parMapN(JwtConfig.apply)
}
