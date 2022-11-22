package babymed.integrations.opersms.domain

import derevo.circe.magnolia.customizableDecoder
import derevo.circe.magnolia.customizableEncoder
import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import io.circe.magnolia.configured.Configuration

import babymed.integrations.opersms.domain.StatusResponse.SmsStatus

@derive(encoder, decoder)
case class StatusResponse(
    messages: List[SmsStatus]
  )

object StatusResponse {
  @derive(customizableEncoder, customizableDecoder)
  case class SmsStatus(
      messageId: Int,
      channel: String,
      status: DeliveryStatus,
      statusDate: String,
    )

  object SmsStatus {
    implicit val configuration: Configuration = Configuration.default.withKebabCaseMemberNames
  }
}
