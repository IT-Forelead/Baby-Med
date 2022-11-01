package babymed.services.users.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import io.circe.Decoder
import io.circe.Encoder
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

@derive(decoder, encoder)
case class UserAndHash(user: User, password: PasswordHash[SCrypt])
object UserAndHash {
  implicit val passwordHashEncoder: Encoder[PasswordHash[SCrypt]] =
    Encoder.encodeString.contramap(_.toString)
  implicit val passwordHashDecoder: Decoder[PasswordHash[SCrypt]] =
    Decoder.decodeString.map(PasswordHash[SCrypt])
}
