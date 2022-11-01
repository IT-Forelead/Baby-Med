package babymed.support.services.syntax

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

import io.circe.Decoder
import io.circe.Encoder
import io.grpc.MethodDescriptor

import babymed.syntax.all.circeSyntaxDecoderOps
import babymed.syntax.all.genericSyntaxGenericTypeOps

trait MarshallerSyntax {
  implicit def codec[T: Encoder: Decoder]: MethodDescriptor.Marshaller[T] =
    new MethodDescriptor.Marshaller[T] {
      override def stream(value: T): InputStream =
        new ByteArrayInputStream(value.toJson.getBytes(StandardCharsets.UTF_8))

      override def parse(stream: InputStream): T = {
        val encodedBytes = Array.ofDim[Byte](stream.available())
        stream.read(encodedBytes)

        new String(encodedBytes, StandardCharsets.UTF_8).as[T]
      }
    }
}
