package babymed.services.users.setup

import cats.effect.Async
import cats.implicits._
import ciris._
import ciris.refined.refTypeConfigDecoder
import eu.timepit.refined.types.string.NonEmptyString

import babymed.domain.AppMode
import babymed.support.services.http4s.HttpServerConfig
import babymed.support.services.rpc.GrpcServerConfig
import babymed.support.skunk.DataBaseConfig
import babymed.syntax.all.circeConfigDecoder
import babymed.syntax.refined.commonSyntaxAutoRefineV
import babymed.util.ConfigDecoders.appModeConfigDecoder

object ConfigLoader {
  def load[F[_]: Async]: F[Config] = (
    env("APP_MODE").as[AppMode],
    GrpcServerConfig.configValues("USERS"),
    HttpServerConfig.configValues("USERS"),
    DataBaseConfig.configValues,
  ).parMapN(Config.apply).load[F]
}
