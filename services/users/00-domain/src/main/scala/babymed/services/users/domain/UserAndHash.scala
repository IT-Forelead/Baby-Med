package babymed.services.users.domain

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt
import io.circe.refined._
import eu.timepit.refined.cats._
import io.circe.{Decoder, Encoder}

@derive(decoder, encoder)
case class UserAndHash(user: User, password: PasswordHash[SCrypt])
object UserAndHash {

  implicit val passwordHashEncoder: Encoder[PasswordHash[SCrypt]] =
    Encoder.encodeString.contramap(_.toString)
  implicit val passwordHashDecoder: Decoder[PasswordHash[SCrypt]] =
    Decoder.decodeString.map(PasswordHash[SCrypt])
}
