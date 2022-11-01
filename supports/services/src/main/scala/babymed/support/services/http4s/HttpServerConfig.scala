package babymed.support.services.http4s

import cats.implicits.catsSyntaxTuple3Parallel
import ciris._
import ciris.refined.refTypeConfigDecoder
import eu.timepit.refined.types.net.NonSystemPortNumber
import eu.timepit.refined.types.string.NonEmptyString

final case class HttpServerConfig(
    httpPort: NonSystemPortNumber,
    logger: LogConfig,
  )
object HttpServerConfig {
  def configValues(serviceName: NonEmptyString): ConfigValue[Effect, HttpServerConfig] = (
    env(s"${serviceName}_HTTP_PORT").as[NonSystemPortNumber],
    env("HTTP_HEADER_LOG").as[Boolean],
    env("HTTP_BODY_LOG").as[Boolean],
  ).parMapN {
    case (port, header, body) =>
      HttpServerConfig(port, LogConfig(header, body))
  }
}
