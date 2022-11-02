package babymed.services.babymed.api

import babymed.services.auth.impl.Security
import babymed.support.services.http4s.HttpServerConfig

case class ServerEnvironment[F[_]](
    services: Services[F],
    config: ServerEnvironment.Config,
    security: Security[F],
  )

object ServerEnvironment {
  case class Config(
      http: HttpServerConfig
    )
}
