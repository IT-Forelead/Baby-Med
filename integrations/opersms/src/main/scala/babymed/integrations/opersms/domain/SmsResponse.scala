package babymed.integrations.opersms.domain;

import java.time.ZonedDateTime

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

@derive(decoder, encoder)
case class SmsResponse(
    recipient: String,
    text: String,
    date_received: ZonedDateTime,
    client_id: String,
    request_id: Int,
    message_id: Int,
    _id: String,
  )
