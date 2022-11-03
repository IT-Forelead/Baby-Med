package babymed.services.babymed.api.setup

import cats.effect.Async
import cats.implicits._
import babymed.domain.AppMode
import babymed.syntax.refined._
import babymed.services.auth.JwtConfig
import babymed.support.redis.RedisConfig
import babymed.support.services.ServiceConfig
import babymed.support.services.http4s.HttpServerConfig
import babymed.syntax.all.circeConfigDecoder
import ciris._

object ConfigLoader {
  private def loadServicesConfig: ConfigValue[Effect, Config.ServicesConfig] = (
    ServiceConfig.configValues("USERS"),
    ServiceConfig.configValues("CUSTOMERS"),
    ServiceConfig.configValues("PAYMENTS"),
  ).parMapN(Config.ServicesConfig.apply)

  def load[F[_]: Async]: F[Config] = (
    env("APP_MODE").as[AppMode],
    HttpServerConfig.configValues("BABYMED_API"),
    JwtConfig.configValues,
    RedisConfig.configValues,
    loadServicesConfig,
  ).parMapN(Config.apply).load[F]
}
