package babymed.services.messages.domain

import java.time.LocalDateTime

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import io.circe.refined._

import babymed.domain.MessageType
import babymed.integrations.opersms.domain.DeliveryStatus
import babymed.refinements.Phone
import babymed.services.messages.domain.types.MessageId
import babymed.services.messages.domain.types.MessageText

@derive(decoder, encoder)
case class Message(
    id: MessageId,
    sentDate: LocalDateTime,
    phone: Phone,
    text: MessageText,
    messageType: MessageType,
    deliveryStatus: DeliveryStatus,
  )
