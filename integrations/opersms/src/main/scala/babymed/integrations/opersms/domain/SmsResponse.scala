package babymed.integrations.opersms.domain

import java.time.ZonedDateTime

import derevo.circe.magnolia.customizableDecoder
import derevo.circe.magnolia.customizableEncoder
import derevo.derive
import io.circe.magnolia.configured.Configuration

@derive(customizableEncoder, customizableDecoder)
case class SmsResponse(
    recipient: String,
    text: String,
    dateReceived: ZonedDateTime,
    clientId: String,
    requestId: Int,
    messageId: Int,
    _id: String,
  )

object SmsResponse {
  implicit val configuration: Configuration = Configuration.default.withSnakeCaseMemberNames
}
