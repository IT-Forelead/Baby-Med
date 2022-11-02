package babymed.services.auth.domain

import scala.concurrent.duration.FiniteDuration

import cats.Eq
import ciris.refined._
import derevo.cats.show
import derevo.derive
import dev.profunktor.auth.jwt.JwtSymmetricAuth
import dev.profunktor.auth.jwt.JwtToken
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.estatico.newtype.macros.newtype

import babymed.util.configDecoder

object types {
  @derive(configDecoder, show)
  @newtype case class JwtAccessTokenKey(secret: NonEmptyString)
  @newtype case class TokenExpiration(value: FiniteDuration)
  @newtype case class UserJwtAuth(value: JwtSymmetricAuth)

  implicit val tokenEq: Eq[JwtToken] = Eq.by(_.value)

  implicit val tokenCodec: Codec[JwtToken] = deriveCodec
}
