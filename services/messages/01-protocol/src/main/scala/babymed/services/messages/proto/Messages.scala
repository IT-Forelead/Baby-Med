package babymed.services.messages.proto

import babymed.domain.DeliveryStatus
import babymed.services.messages.domain.CreateMessage
import babymed.services.messages.domain.Message
import babymed.services.messages.domain.types.MessageId
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec

@service(Custom)
trait Messages[F[_]] {
  def create(createMessage: CreateMessage): F[Message]
  def changeStatus(id: MessageId, deliveryStatus: DeliveryStatus): F[Message]
}

object Messages {}
