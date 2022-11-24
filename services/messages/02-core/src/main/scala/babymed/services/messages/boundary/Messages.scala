package babymed.services.messages.boundary

import cats.Monad
import cats.implicits._

import babymed.integrations.opersms.OperSmsClient
import babymed.integrations.opersms.domain.DeliveryStatus
import babymed.services.messages.domain.CreateMessage
import babymed.services.messages.domain.Message
import babymed.services.messages.domain.types.MessageId
import babymed.services.messages.proto
import babymed.services.messages.repositories.MessagesRepository

class Messages[F[_]: Monad](
    messagesRepository: MessagesRepository[F],
    operSmsClient: OperSmsClient[F],
  ) extends proto.Messages[F] {
  override def send(createMessage: CreateMessage): F[Message] =
    for {
      cm <- messagesRepository.create(createMessage)
      _ <- operSmsClient.send(createMessage.phone, createMessage.text.value)
    } yield cm

  override def changeStatus(id: MessageId, deliveryStatus: DeliveryStatus): F[Message] =
    messagesRepository.changeStatus(id, deliveryStatus)
}
