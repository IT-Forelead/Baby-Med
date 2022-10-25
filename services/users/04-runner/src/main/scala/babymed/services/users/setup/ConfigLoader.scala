package babymed.services.users.setup

import cats.effect.Async
import cats.implicits._
import babymed.domain.AppMode
import babymed.syntax.refined.commonSyntaxAutoRefineV
import babymed.support.services.http4s.HttpServerConfig
import babymed.support.services.rpc.GrpcServerConfig
import babymed.support.skunk.DataBaseConfig
import babymed.syntax.all.circeConfigDecoder
import ciris.env
import ciris.refined.refTypeConfigDecoder
import eu.timepit.refined.types.string.NonEmptyString

object ConfigLoader {
  def load[F[_]: Async]: F[Config] = (
    env("APP_MODE").as[AppMode],
    env("USERS_SERVICE_HOST_NAME")
      .as[NonEmptyString]
      .option
      .map(_.getOrElse[NonEmptyString]("127.0.0.1")),
    GrpcServerConfig.configValues("USERS"),
    HttpServerConfig.configValues("USERS"),
    DataBaseConfig.configValues,
  ).parMapN(Config.apply).load[F]
}
