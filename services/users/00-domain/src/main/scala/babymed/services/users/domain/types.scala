package babymed.services.users.domain

import cats.instances.uuid
import derevo.cats.{eqv, show}
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import eu.timepit.refined.types.all.NonEmptyString
import io.estatico.newtype.macros.newtype

import java.util.UUID
import javax.crypto.Cipher

object types {

  @derive(decoder, encoder, eqv, show, uuid)
  @newtype case class UserId(value: UUID)

  @derive(decoder, encoder, eqv)
  @newtype case class FirstName(value: NonEmptyString)

  @derive(decoder, encoder, eqv)
  @newtype case class LastName(value: NonEmptyString)

  @derive(decoder, encoder, eqv, show)
  @newtype case class EncryptedPassword(value: String)

  @newtype case class EncryptCipher(value: Cipher)

  @newtype case class DecryptCipher(value: Cipher)

}
