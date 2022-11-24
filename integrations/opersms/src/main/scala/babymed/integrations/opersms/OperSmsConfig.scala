package babymed.integrations.opersms

import java.net.URI

import cats.implicits._
import ciris._
import ciris.refined.refTypeConfigDecoder
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString

import babymed.util.ConfigDecoders.javaNetUriConfigDecoder

case class OperSmsConfig(
    apiURL: URI,
    login: NonEmptyString,
    password: Secret[NonEmptyString],
    enabled: Boolean = false,
  )

object OperSmsConfig {
  def configValues: ConfigValue[Effect, OperSmsConfig] = (
    env("MESSAGE_BROKER_API").as[URI],
    env("MESSAGE_BROKER_USERNAME").as[NonEmptyString],
    env("MESSAGE_BROKER_PASSWORD").as[NonEmptyString].secret,
    env("MESSAGE_BROKER_ENABLED").as[Boolean],
  ).parMapN(OperSmsConfig.apply)
}
