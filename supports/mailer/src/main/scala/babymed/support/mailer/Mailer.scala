package babymed.support.mailer

import java.util.Properties
import javax.mail.Message.RecipientType._
import javax.mail._
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.DurationLong
import scala.jdk.CollectionConverters.MapHasAsJava

import cats.effect.Async
import cats.effect.Sync
import cats.implicits._
import babymed.support.mailer.data.Props.SmtpConnectionTimeoutKey
import babymed.support.mailer.data._
import babymed.support.mailer.exception.DeliverFailure.AuthenticationFailed
import babymed.support.mailer.exception.InvalidAddress
import babymed.support.mailer.retries.Retry
import org.typelevel.log4cats.Logger
import retry.RetryPolicies.exponentialBackoff
import retry.RetryPolicies.limitRetries
import retry.RetryPolicy
import eu.timepit.refined.auto.autoUnwrap

trait Mailer[F[_]] {
  def send(email: Email): F[Unit]
}
object Mailer {
  def apply[F[_]: Async: Logger](props: Props, credentials: Credentials): Mailer[F] =
    new MailerImpl[F](props, credentials)

  def default[F[_]: Async: Logger](credentials: Credentials): Mailer[F] =
    new MailerImpl[F](Props.default, credentials)

  private class MailerImpl[F[_]: Async](
      props: Props,
      credentials: Credentials,
    )(implicit
      logger: Logger[F]
    ) extends Mailer[F] {
    private[mailer] val retryPolicy: RetryPolicy[F] = {
      val delay = props.values.get(SmtpConnectionTimeoutKey).fold(1.second)(_.toLong.millis)
      limitRetries[F](5) |+| exponentialBackoff[F](delay)
    }

    private[mailer] val properties: Properties = {
      val properties = System.getProperties
      properties.putAll(props.values.asJava)
      properties
    }

    private[mailer] val authenticator: F[Authenticator] =
      Sync[F].delay(
        new Authenticator {
          override def getPasswordAuthentication: PasswordAuthentication =
            new PasswordAuthentication(credentials.user.value, credentials.password.value)
        }
      )

    private[mailer] def session(properties: Properties, auth: Authenticator): F[Session] =
      Sync[F].delay(Session.getDefaultInstance(properties, auth))

    private[mailer] def prepTextPart(text: Text): MimeBodyPart = {
      val part = new MimeBodyPart()
      part.setText(text.value, text.charset.toString, text.subtype.value)
      text.headers.foreach(header => part.setHeader(header.name, header.value))
      part
    }

    private[mailer] def prepHtmlPart(html: Html): MimeBodyPart = {
      val part = new MimeBodyPart()
      part.setText(html.value, html.charset.toString, html.subtype.value)
      html.headers.foreach(header => part.setHeader(header.name, header.value))
      part
    }

    private[mailer] def prepareMessage(session: Session, email: Email): MimeMessage = {
      val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(email.from.value))
      email.to.map(ads => message.addRecipient(TO, new InternetAddress(ads.value)))
      email.cc.foreach(ads => message.addRecipient(CC, new InternetAddress(ads.value)))
      email.bcc.foreach(ads => message.addRecipient(BCC, new InternetAddress(ads.value)))
      message.setSubject(email.subject.value)
      val bodyParts = List(
        email.content.text.map(prepTextPart),
        email.content.html.map(prepHtmlPart),
      ).flatten
      message.setContent(new MimeMultipart {
        bodyParts.foreach(addBodyPart)
      })
      email.headers.foreach(header => message.setHeader(header.name, header.value))
      message
    }

    override def send(email: Email): F[Unit] =
      for {
        auth <- authenticator
        session <- session(properties, auth)
        message = prepareMessage(session, email)
        _ <- Logger[F].info(
          s"Starting sending email: from [${email.from}] subject [${email.subject}]"
        )
        task = Sync[F].delay(Transport.send(message))
        result <- Retry[F]
          .retry(retryPolicy)(task)
          .adaptError {
            case exception: AuthenticationFailedException =>
              AuthenticationFailed(exception.getMessage)
            case exception: SendFailedException =>
              InvalidAddress(exception.getMessage)
          }
        _ <- Logger[F].info(
          s"Finished sending email: from [${email.from}] subject [${email.subject}]"
        )
      } yield result
  }
}
