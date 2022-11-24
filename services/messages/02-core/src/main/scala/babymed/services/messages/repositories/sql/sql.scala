package babymed.services.messages.repositories

import eu.timepit.refined.types.string.NonEmptyString
import skunk._
import skunk.codec.all.uuid
import skunk.codec.all.varchar

import babymed.domain.MessageType
import babymed.effects.IsUUID
import babymed.integrations.opersms.domain.DeliveryStatus
import babymed.services.messages.domain.types.MessageId
import babymed.services.messages.domain.types.MessageText

package object sql {
  def identity[A: IsUUID]: Codec[A] = uuid.imap[A](IsUUID[A].uuid.get)(IsUUID[A].uuid.apply)

  val nes: Codec[NonEmptyString] = varchar.imap[NonEmptyString](NonEmptyString.unsafeFrom)(_.value)
  val messageId: Codec[MessageId] = identity[MessageId]
  val messageText: Codec[MessageText] = nes.imap[MessageText](MessageText.apply)(_.value)
  val deliveryStatus: Codec[DeliveryStatus] =
    varchar.eimap[DeliveryStatus](str =>
      DeliveryStatus.values.find(_.entryName == str).toRight("type not found")
    )(_.entryName)
  val messageType: Codec[MessageType] =
    varchar.eimap[MessageType](str =>
      MessageType.values.find(_.value == str).toRight("type not found ")
    )(_.value)
}
