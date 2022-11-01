package babymed.support.mailer.retries

import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.mail.MessagingException

import cats.Applicative
import cats.effect.Temporal
import org.typelevel.log4cats.Logger
import retry.RetryDetails._
import retry._

trait Retry[F[_]] {
  def retry[A](policy: RetryPolicy[F])(fa: F[A]): F[A]
}

object Retry {
  def apply[F[_]: Retry]: Retry[F] = implicitly

  implicit def forLoggerTemporal[F[_]: Temporal](implicit logger: Logger[F]): Retry[F] =
    new Retry[F] {
      def retry[A](policy: RetryPolicy[F])(fa: F[A]): F[A] = {
        def onError(e: Throwable, details: RetryDetails): F[Unit] =
          details match {
            case WillDelayAndRetry(_, retriesSoFar, _) =>
              logger.warn(
                s"Failed to process send email with ${e.getMessage}. So far we have retried $retriesSoFar times."
              )
            case GivingUp(totalRetries, _) =>
              logger.warn(s"Giving up on send email after $totalRetries retries.")
          }

        def isWorthRetrying: Throwable => F[Boolean] = {
          case exception: MessagingException =>
            exception.getCause match {
              case _: UnknownHostException => Applicative[F].pure(true)
              case _: SocketTimeoutException => Applicative[F].pure(true)
              case _ => Applicative[F].pure(false)
            }

          case _ => Applicative[F].pure(false)
        }

        retryingOnSomeErrors[A](policy, isWorthRetrying, onError)(fa)
      }
    }
}
