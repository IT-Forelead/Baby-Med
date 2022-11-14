package babymed.services.babymed.api.setup

import babymed.domain.AppMode
import babymed.services.auth.JwtConfig
import babymed.services.babymed.api.ServerEnvironment
import babymed.support.redis.RedisConfig
import babymed.support.services.ServiceConfig
import babymed.support.services.http4s.HttpServerConfig

case class Config(
    appMode: AppMode,
    httpServer: HttpServerConfig,
    jwtConfig: JwtConfig,
    redis: RedisConfig,
    services: Config.ServicesConfig,
  ) {
  lazy val toServer: ServerEnvironment.Config =
    ServerEnvironment.Config(httpServer)
}

object Config {
  case class ServicesConfig(
      users: ServiceConfig,
      visits: ServiceConfig,
    )
}
