package babymed.domain

import io.circe._
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder

case class ResponseData[A](
    data: List[A],
    total: Long,
  )

object ResponseData {
  implicit def enc[A: Encoder]: Encoder[ResponseData[A]] = deriveEncoder[ResponseData[A]]
  implicit def dec[A: Decoder]: Decoder[ResponseData[A]] = deriveDecoder[ResponseData[A]]
}
