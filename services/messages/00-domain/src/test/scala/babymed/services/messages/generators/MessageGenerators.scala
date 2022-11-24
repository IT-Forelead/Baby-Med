package babymed.services.messages.generators

import org.scalacheck.Gen

import babymed.services.messages.domain.CreateMessage
import babymed.services.messages.domain.Message

trait MessageGenerators extends TypeGen {
  val messageGen: Gen[Message] =
    for {
      id <- messageIdGen
      sentDate <- localDateTimeGen
      phone <- phoneGen
      text <- messageTextGen
      messageType <- messageTypeGen
      deliveryStatus <- deliveryStatusGen
    } yield Message(id, sentDate, phone, text, messageType, deliveryStatus)

  val createMessageGen: Gen[CreateMessage] =
    for {
      phone <- phoneGen
      text <- messageTextGen
      messageType <- messageTypeGen
      deliveryStatus <- deliveryStatusGen
    } yield CreateMessage(phone, text, messageType, deliveryStatus)
}
