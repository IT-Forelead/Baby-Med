package babymed.support.services.syntax

import java.time.{LocalDateTime, ZonedDateTime}
import babymed.exception.MultipartDecodeError
import babymed.support.services.http4s.utils.MapConvert
import babymed.support.services.http4s.utils.MapConvert.ValidationResult
import cats.MonadThrow
import cats.effect.Async
import cats.effect.Sync
import cats.implicits._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Decoder
import io.circe.Encoder
import io.circe.syntax.EncoderOps
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Authorization
import org.http4s.headers.`Content-Type`
import org.http4s.multipart.Part
import org.typelevel.log4cats.Logger

trait Http4sSyntax {
  implicit def http4SyntaxReqOps[F[_]: JsonDecoder: MonadThrow](
      request: Request[F]
    ): RequestOps[F] =
    new RequestOps(request)
  implicit def http4SyntaxPartOps[F[_]](parts: Vector[Part[F]]): PartOps[F] =
    new PartOps(parts)

  implicit def http4SyntaxGenericTypeOps[A](obj: A): GenericTypeOps[A] =
    new GenericTypeOps[A](obj)

  implicit def deriveEntityEncoder[F[_]: Async, A: Encoder]: EntityEncoder[F, A] =
    jsonEncoderOf[F, A]

  implicit def deriveEntityDecoder[F[_]: Async, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]

  implicit val zonedDateTimeQueryParamDecoder: QueryParamDecoder[ZonedDateTime] =
    QueryParamDecoder[String].map(ZonedDateTime.parse)

  implicit val localDateTimeQueryParamDecoder: QueryParamDecoder[LocalDateTime] =
    QueryParamDecoder[String].map(LocalDateTime.parse)
}

final class RequestOps[F[_]: JsonDecoder: MonadThrow](private val request: Request[F])
    extends Http4sDsl[F] {
  def decodeR[A: Decoder](f: A => F[Response[F]])(implicit logger: Logger[F]): F[Response[F]] =
    request
      .asJsonDecode[A]
      .attempt
      .flatMap {
        case Right(a) => f(a)
        case Left(error) if error.getMessage.startsWith("Predicate") =>
          BadRequest(error.getMessage)
        case Left(error) =>
          logger.warn(error)("Decode failure") *> UnprocessableEntity()
      }
      .handleErrorWith { error =>
        logger.error(error)("Error occurred while handle request") *>
          BadRequest("Something went wrong. Please try again a few minutes")
      }

  def bearer(token: NonEmptyString): Request[F] =
    request.putHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, token.value)))
}

final class PartOps[F[_]](private val parts: Vector[Part[F]]) {
  private def filterFileTypes(part: Part[F]): Boolean = part.filename.exists(_.trim.nonEmpty)

  def fileParts: Vector[Part[F]] = parts.filter(filterFileTypes)

  def fileParts(mediaType: MediaType): Vector[Part[F]] =
    parts.filter(_.headers.get[`Content-Type`].exists(_.mediaType == mediaType))

  def isFilePartExists: Boolean = parts.exists(filterFileTypes)

  def textParts: Vector[Part[F]] = parts.filterNot(filterFileTypes)

  def convert[A](implicit mapper: MapConvert[F, ValidationResult[A]], F: Sync[F]): F[A] =
    for {
      collectKV <- textParts.traverse { part =>
        part.bodyText.compile.foldMonoid.map(part.name.get -> _)
      }
      entity <- mapper.fromMap(collectKV.toMap)
      validEntity <- entity.fold(
        error => F.raiseError[A](MultipartDecodeError(error.toList.mkString(" | "))),
        success => success.pure[F],
      )
    } yield validEntity
}

final class GenericTypeOps[A](obj: A) {
  def toFormData[F[_]](implicit encoder: Encoder.AsObject[A]): Vector[Part[F]] =
    obj
      .asJsonObject
      .toVector
      .map {
        case k -> v =>
          k -> v.asString
      }
      .collect {
        case k -> Some(v) =>
          Part.formData[F](k, v)
      }
}
