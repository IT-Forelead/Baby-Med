package babymed.services.messages.setup

import cats.effect.Async
import cats.implicits._

import babymed.support.services.http4s.HttpServerConfig
import babymed.support.services.rpc.GrpcServerConfig
import babymed.support.skunk.DataBaseConfig
import babymed.syntax.refined.commonSyntaxAutoRefineV

object ConfigLoader {
  def load[F[_]: Async]: F[Config] = (
    GrpcServerConfig.configValues("MESSAGES"),
    HttpServerConfig.configValues("MESSAGES"),
    DataBaseConfig.configValues,
  ).parMapN(Config.apply).load[F]
}
