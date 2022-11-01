package babymed.support.services

import cats.implicits.catsSyntaxTuple2Parallel
import ciris._
import ciris.refined.refTypeConfigDecoder
import eu.timepit.refined.auto.autoUnwrap
import eu.timepit.refined.types.net.NonSystemPortNumber
import eu.timepit.refined.types.string.NonEmptyString
import higherkindness.mu.rpc.ChannelForAddress

final case class ServiceConfig(host: NonEmptyString, port: NonSystemPortNumber) {
  lazy val channelAddress: ChannelForAddress = ChannelForAddress(host, port)
}

object ServiceConfig {
  def configValues(serviceName: NonEmptyString): ConfigValue[Effect, ServiceConfig] = (
    env(s"${serviceName}_RPC_HOST").as[NonEmptyString],
    env(s"${serviceName}_RPC_PORT").as[NonSystemPortNumber],
  ).parMapN(ServiceConfig.apply)
}
