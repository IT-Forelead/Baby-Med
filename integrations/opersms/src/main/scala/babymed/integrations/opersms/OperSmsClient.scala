package babymed.integrations.opersms

import cats.data.NonEmptyList
import cats.data.OptionT
import cats.effect.Sync
import cats.implicits.toFunctorOps
import eu.timepit.refined.types.string.NonEmptyString
import org.typelevel.log4cats.Logger

import babymed.integrations.opersms.domain.RequestId
import babymed.integrations.opersms.domain.SMS
import babymed.integrations.opersms.domain.SmsResponse
import babymed.integrations.opersms.requests.CheckStatus
import babymed.integrations.opersms.requests.SendSms
import babymed.refinements.Phone
import babymed.support.sttp.SttpBackends
import babymed.support.sttp.SttpClient
import babymed.support.sttp.SttpClientAuth

trait OperSmsClient[F[_]] {
  def send(phone: Phone, text: NonEmptyString): F[Unit]
}

object OperSmsClient {
  def make[F[_]: Sync: SttpBackends.Simple: Logger](
      config: OperSmsConfig
    ): OperSmsClient[F] =
    if (config.enabled)
      new NoOpOperSmsClientImpl[F]
    else
      new OperSmsClientImpl[F](config)

  private class NoOpOperSmsClientImpl[F[_]: Logger] extends OperSmsClient[F] {
    override def send(phone: Phone, text: NonEmptyString): F[Unit] =
      Logger[F].info(s"""Message sent to [$phone], message text [ \n$text\n ]""")
  }

  private class OperSmsClientImpl[F[_]: Sync: SttpBackends.Simple: Logger](config: OperSmsConfig)
      extends OperSmsClient[F] {
    private lazy val client: SttpClient.CirceJson[F] = SttpClient.circeJson(
      config.apiURL,
      SttpClientAuth.noAuth,
    )
    override def send(phone: Phone, text: NonEmptyString): F[Unit] =
      OptionT(
        client
          .request(
            SendSms(
              config.login,
              config.password.value,
              NonEmptyList
                .one(SMS.unPlus(phone, text)),
            )
          )
          .map(_.headOption)
      )
        .semiflatMap(checkSmsStatus)
        .value
        .void

    private def checkSmsStatus(smsResponse: SmsResponse): F[Unit] =
      client
        .request(
          CheckStatus(
            config.login,
            config.password.value,
            NonEmptyList
              .one(RequestId(smsResponse.requestId)),
          )
        )
        .void
  }
}
