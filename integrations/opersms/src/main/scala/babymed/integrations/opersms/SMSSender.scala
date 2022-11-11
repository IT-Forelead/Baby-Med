package babymed.integrations.opersms

import cats.data.NonEmptyList
import cats.effect.Sync
import cats.implicits.toFunctorOps
import eu.timepit.refined.types.string.NonEmptyString
import org.typelevel.log4cats.Logger

import babymed.integrations.opersms.domain.SMS
import babymed.integrations.opersms.requests.SendSms
import babymed.refinements.Phone
import babymed.support.sttp.SttpBackends
import babymed.support.sttp.SttpClient
import babymed.support.sttp.SttpClientAuth

trait SMSSender[F[_]] {
  def send(phone: Phone, text: NonEmptyString): F[Unit]
}

object SMSSender {
  def make[F[_]: Sync: SttpBackends.Simple: Logger](
      config: OperSmsConfig
    ): SMSSender[F] =
    if (config.enabled)
      new NoOpSMSSenderImpl[F]
    else
      new SMSSenderImpl[F](config)

  private class NoOpSMSSenderImpl[F[_]: Logger] extends SMSSender[F] {
    override def send(phone: Phone, text: NonEmptyString): F[Unit] =
      Logger[F].info(s"""NotificationMessage message sent to [$phone], message text [$text]""")
  }

  private class SMSSenderImpl[F[_]: Sync: SttpBackends.Simple: Logger](config: OperSmsConfig)
      extends SMSSender[F] {
    private lazy val client: SttpClient.CirceJson[F] = SttpClient.circeJson(
      config.apiURL,
      SttpClientAuth.noAuth,
    )
    override def send(phone: Phone, text: NonEmptyString): F[Unit] =
      client
        .request(
          SendSms(
            config.login,
            config.password.value,
            NonEmptyList
              .one(SMS.unPlus(phone, text)),
          )
        )
        .void
  }
}
