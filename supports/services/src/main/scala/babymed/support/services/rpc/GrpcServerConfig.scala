package babymed.support.services.rpc

import ciris._
import ciris.refined.refTypeConfigDecoder
import eu.timepit.refined.types.net.NonSystemPortNumber
import eu.timepit.refined.types.string.NonEmptyString

case class GrpcServerConfig(grpcPort: NonSystemPortNumber)

object GrpcServerConfig {
  def configValues(serviceName: NonEmptyString): ConfigValue[Effect, GrpcServerConfig] =
    env(s"${serviceName}_RPC_PORT")
      .as[NonSystemPortNumber]
      .map(GrpcServerConfig.apply)
}
