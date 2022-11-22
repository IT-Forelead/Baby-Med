package babymed.services.messages.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import io.circe.refined._

import babymed.domain.DeliveryStatus
import babymed.domain.MessageType
import babymed.refinements.Phone
import babymed.services.messages.domain.types.MessageText

@derive(decoder, encoder)
case class CreateMessage(
    phone: Phone,
    text: MessageText,
    messageType: MessageType,
    deliveryStatus: DeliveryStatus,
  )
