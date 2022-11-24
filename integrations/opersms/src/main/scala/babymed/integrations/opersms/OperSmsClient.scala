package babymed.integrations.opersms

import scala.concurrent.duration.DurationInt

import cats.data.NonEmptyList
import cats.data.OptionT
import cats.effect.Async
import cats.effect.Sync
import cats.implicits.catsSyntaxSemigroup
import cats.implicits.toFunctorOps
import eu.timepit.refined.types.string.NonEmptyString
import org.typelevel.log4cats.Logger
import retry.RetryPolicies.exponentialBackoff
import retry.RetryPolicies.limitRetries
import retry.RetryPolicy

import babymed.integrations.opersms.domain.DeliveryStatus
import babymed.integrations.opersms.domain.RequestId
import babymed.integrations.opersms.domain.SMS
import babymed.integrations.opersms.domain.SmsResponse
import babymed.integrations.opersms.requests.CheckStatus
import babymed.integrations.opersms.requests.SendSms
import babymed.integrations.opersms.retries.Retry
import babymed.refinements.Phone
import babymed.support.sttp.SttpBackends
import babymed.support.sttp.SttpClient
import babymed.support.sttp.SttpClientAuth

trait OperSmsClient[F[_]] {
  def send(
      phone: Phone,
      text: NonEmptyString,
      changeStatus: DeliveryStatus => F[Unit],
    ): F[Unit]
}

object OperSmsClient {
  def make[F[_]: Async: SttpBackends.Simple: Logger](
      config: OperSmsConfig
    ): OperSmsClient[F] =
    if (config.enabled)
      new OperSmsClientImpl[F](config)
    else
      new NoOpOperSmsClientImpl[F]

  private class NoOpOperSmsClientImpl[F[_]: Logger] extends OperSmsClient[F] {
    override def send(
        phone: Phone,
        text: NonEmptyString,
        changeStatus: DeliveryStatus => F[Unit],
      ): F[Unit] =
      Logger[F].info(s"""Message sent to [$phone], message text [ \n$text\n ]""")
  }

  private class OperSmsClientImpl[F[_]: Async: SttpBackends.Simple: Logger](config: OperSmsConfig)
      extends OperSmsClient[F] {
    private val retryPolicy: RetryPolicy[F] =
      limitRetries[F](3) |+| exponentialBackoff[F](1.minutes)

    private lazy val client: SttpClient.CirceJson[F] = SttpClient.circeJson(
      config.apiURL,
      SttpClientAuth.noAuth,
    )
    override def send(
        phone: Phone,
        text: NonEmptyString,
        changeStatus: DeliveryStatus => F[Unit],
      ): F[Unit] =
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
        .semiflatMap(checkSmsStatus(_, changeStatus))
        .value
        .void

    private def checkSmsStatus(
        smsResponse: SmsResponse,
        changeStatus: DeliveryStatus => F[Unit],
      ): F[Unit] = {
      val task =
        OptionT(
          client
            .request(
              CheckStatus(
                config.login,
                config.password.value,
                NonEmptyList
                  .one(RequestId(smsResponse.requestId)),
              )
            )
            .map(_.messages.headOption)
        )
          .semiflatMap(smsStatus => changeStatus(smsStatus.status))
          .getOrElseF(Sync[F].unit)
      Retry[F].retry(retryPolicy)(task)
    }
  }
}
