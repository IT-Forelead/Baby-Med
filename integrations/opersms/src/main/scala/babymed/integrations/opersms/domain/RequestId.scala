package babymed.integrations.opersms.domain

import derevo.circe.magnolia.customizableDecoder
import derevo.circe.magnolia.customizableEncoder
import derevo.derive
import io.circe.magnolia.configured.Configuration

@derive(customizableEncoder, customizableDecoder)
case class RequestId(
    requestId: Int
  )
object RequestId {
  implicit val configuration: Configuration = Configuration.default.withSnakeCaseMemberNames
}
