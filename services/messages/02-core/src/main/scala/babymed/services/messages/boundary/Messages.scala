package babymed.services.messages.boundary

import cats.Monad

import babymed.domain.DeliveryStatus
import babymed.services.messages.domain.CreateMessage
import babymed.services.messages.domain.Message
import babymed.services.messages.domain.types.MessageId
import babymed.services.messages.proto
import babymed.services.messages.repositories.MessagesRepository

class Messages[F[_]: Monad](messagesRepository: MessagesRepository[F]) extends proto.Messages[F] {
  override def create(createMessage: CreateMessage): F[Message] =
    messagesRepository.create(createMessage)
  override def changeStatus(id: MessageId, deliveryStatus: DeliveryStatus): F[Message] =
    messagesRepository.changeStatus(id, deliveryStatus)
}
