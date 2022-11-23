package babymed.services.users.setup

import cats.effect.Async
import cats.implicits._
import ciris.ConfigValue
import ciris.Effect

import babymed.support.services.ServiceConfig
import babymed.support.services.http4s.HttpServerConfig
import babymed.support.services.rpc.GrpcServerConfig
import babymed.support.skunk.DataBaseConfig
import babymed.syntax.refined.commonSyntaxAutoRefineV

object ConfigLoader {
  private def loadServicesConfig: ConfigValue[Effect, Config.ServicesConfig] =
    ServiceConfig
      .configValues("MESSAGES")
      .map(Config.ServicesConfig.apply)

  def load[F[_]: Async]: F[Config] = (
    GrpcServerConfig.configValues("USERS"),
    HttpServerConfig.configValues("USERS"),
    DataBaseConfig.configValues,
    loadServicesConfig,
  ).parMapN(Config.apply).load[F]
}
