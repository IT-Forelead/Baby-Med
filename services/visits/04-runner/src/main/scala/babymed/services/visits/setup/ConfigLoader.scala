package babymed.services.visits.setup

import cats.effect.Async
import cats.implicits._
import ciris._

import babymed.domain.AppMode
import babymed.support.services.http4s.HttpServerConfig
import babymed.support.services.rpc.GrpcServerConfig
import babymed.support.skunk.DataBaseConfig
import babymed.syntax.refined.commonSyntaxAutoRefineV
import babymed.util.ConfigDecoders.appModeConfigDecoder

object ConfigLoader {
  def load[F[_]: Async]: F[Config] = (
    env("APP_MODE").as[AppMode],
    GrpcServerConfig.configValues("VISITS"),
    HttpServerConfig.configValues("VISITS"),
    DataBaseConfig.configValues,
  ).parMapN(Config.apply).load[F]
}
