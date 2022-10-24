package babymed.support.mailer

import cats.implicits.catsSyntaxTuple6Parallel
import babymed.EmailAddress
import babymed.syntax.config.listConfigDecoder
import babymed.support.mailer.data.types.Host
import babymed.support.mailer.data.types.Password
import ciris._
import ciris.refined.refTypeConfigDecoder
import eu.timepit.refined.types.net.SystemPortNumber
import io.circe.refined._

case class MailerConfig(
    host: Host,
    port: SystemPortNumber,
    username: EmailAddress,
    password: Secret[Password],
    fromAddress: EmailAddress,
    recipients: List[EmailAddress],
  )
object MailerConfig {

  def configValues(serviceName: String): ConfigValue[Effect, MailerConfig] = (
    env(s"${serviceName}_MAILER_HOST").as[Host],
    env(s"${serviceName}_MAILER_PORT").as[SystemPortNumber],
    env(s"${serviceName}_MAILER_USERNAME").as[EmailAddress],
    env(s"${serviceName}_MAILER_PASSWORD").as[Password].secret,
    env(s"${serviceName}_MAILER_FROM_ADDRESS").as[EmailAddress],
    env(s"${serviceName}_MAILER_RECIPIENTS").as[List[EmailAddress]],
  ).parMapN(MailerConfig.apply)
}
