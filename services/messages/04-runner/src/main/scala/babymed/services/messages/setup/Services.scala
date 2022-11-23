package babymed.services.messages.setup

import cats.effect.Async
import cats.effect.Resource
import org.typelevel.log4cats.Logger
import sttp.client3.asynchttpclient.fs2.AsyncHttpClientFs2Backend

import babymed.integrations.opersms.OperSmsClient
import babymed.integrations.opersms.OperSmsConfig

final case class Services[F[_]](
    operSmsClient: OperSmsClient[F]
  )

object Services {
  private def makeOperSms[F[_]: Async: Logger](
      operSmsConfig: OperSmsConfig
    ): Resource[F, OperSmsClient[F]] =
    AsyncHttpClientFs2Backend.resource().map { implicit asyncBackend =>
      OperSmsClient.make[F](operSmsConfig)
    }

  def make[F[_]: Async: Logger](
      operSmsConfig: OperSmsConfig
    ): Resource[F, Services[F]] =
    makeOperSms[F](operSmsConfig).map(Services[F])
}
