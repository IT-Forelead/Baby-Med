package babymed.services.messages.repositories.sql

import java.time.LocalDateTime

import skunk.Decoder
import skunk.Encoder
import skunk.Query
import skunk.codec.all.timestamp
import skunk.implicits.toIdOps
import skunk.implicits.toStringOps
import skunk.~

import babymed.integrations.opersms.domain.DeliveryStatus
import babymed.services.messages.domain.CreateMessage
import babymed.services.messages.domain.Message
import babymed.services.messages.domain.types.MessageId
import babymed.support.skunk.codecs.phone

object MessagesSql {
  private val Columns = messageId ~ timestamp ~ phone ~ messageText ~ messageType ~ deliveryStatus

  val encoder: Encoder[MessageId ~ LocalDateTime ~ CreateMessage] = Columns.contramap {
    case id ~ createdAt ~ cm =>
      id ~ createdAt ~ cm.phone ~ cm.text ~ cm.messageType ~ cm.deliveryStatus
  }

  val decoder: Decoder[Message] = Columns.map {
    case id ~ sentDate ~ phone ~ text ~ messageType ~ deliveryStatus =>
      Message(id, sentDate, phone, text, messageType, deliveryStatus)
  }

  val insert: Query[MessageId ~ LocalDateTime ~ CreateMessage, Message] =
    sql"""INSERT INTO sms_messages VALUES ($encoder) RETURNING *""".query(decoder)

  val changeStatusSql: Query[DeliveryStatus ~ MessageId, Message] =
    sql"""UPDATE sms_messages SET delivery_status = $deliveryStatus WHERE id = $messageId RETURNING *"""
      .query(decoder)
}
