package babymed.support.services.rpc

import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto._

case class RpcRequest[A](context: Map[String, String], request: A)

object RpcRequest {
  implicit def encoder[A: Encoder]: Encoder.AsObject[RpcRequest[A]] = deriveEncoder
  implicit def decoder[A: Decoder]: Decoder[RpcRequest[A]] = deriveDecoder
}
