package babymed.services.messages.repositories

import cats.effect.Concurrent
import cats.effect.MonadCancel
import cats.effect.Resource
import cats.implicits._
import skunk.Session
import skunk.implicits.toIdOps

import babymed.domain.ID
import babymed.effects.Calendar
import babymed.effects.GenUUID
import babymed.integrations.opersms.domain.DeliveryStatus
import babymed.services.messages.domain.CreateMessage
import babymed.services.messages.domain.Message
import babymed.services.messages.domain.types.MessageId
import babymed.support.skunk.syntax.all.skunkSyntaxQueryOps

trait MessagesRepository[F[_]] {
  def create(createMessage: CreateMessage): F[Message]
  def changeStatus(id: MessageId, deliveryStatus: DeliveryStatus): F[Message]
}

object MessagesRepository {
  def make[F[_]: GenUUID: Calendar: Concurrent](
      implicit
      session: Resource[F, Session[F]],
      F: MonadCancel[F, Throwable],
    ): MessagesRepository[F] = new MessagesRepository[F] {
    import sql.MessagesSql._

    override def create(createMessage: CreateMessage): F[Message] =
      for {
        id <- ID.make[F, MessageId]
        now <- Calendar[F].currentDateTime
        message <- insert.queryUnique(id ~ now ~ createMessage)
      } yield message

    override def changeStatus(id: MessageId, deliveryStatus: DeliveryStatus): F[Message] =
      changeStatusSql.queryUnique(deliveryStatus ~ id)
  }
}
