package babymed.services.payments.setup

import babymed.domain.AppMode
import babymed.support.services.http4s.HttpServerConfig
import babymed.support.services.rpc.GrpcServerConfig
import babymed.support.skunk.DataBaseConfig
import eu.timepit.refined.types.string.NonEmptyString

case class Config(
    appMode: AppMode,
    serviceIpAddress: NonEmptyString,
    rpcServer: GrpcServerConfig,
    httpServer: HttpServerConfig,
    database: DataBaseConfig,
  )
