package babymed.syntax

import scala.language.implicitConversions

import io.circe.Decoder
import io.circe.Encoder
import io.circe.HCursor
import io.circe.Json
import io.circe.parser.decode

trait CirceSyntax {
  implicit def circeSyntaxDecoderOps(s: String): DecoderOps = new DecoderOps(s)
  implicit def circeSyntaxJsonDecoderOps(json: Json): JsonDecoderOps = new JsonDecoderOps(json)

  implicit def mapEncoder[K: Encoder, V: Encoder]: Encoder[Map[K, V]] =
    (map: Map[K, V]) => Encoder[List[(K, V)]].apply(map.toList)

  implicit def mapDecoder[K: Decoder, V: Decoder]: Decoder[Map[K, V]] =
    (c: HCursor) => c.as[List[(K, V)]].map(_.toMap)
}

final class DecoderOps(private val s: String) {
  def as[A: Decoder]: A = decode[A](s).fold(throw _, json => json)
}
final class JsonDecoderOps(json: Json) {
  def decodeAs[A](implicit decoder: Decoder[A]): A =
    decoder.decodeJson(json).fold(throw _, json => json)
}
