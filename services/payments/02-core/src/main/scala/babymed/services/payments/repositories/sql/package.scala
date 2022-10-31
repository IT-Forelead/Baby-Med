package babymed.services.payments.repositories

import babymed.domain.Role
import babymed.effects.IsUUID
import babymed.services.payments.domain.types.UZS
import babymed.services.users.domain.types.{Address, FirstName, LastName}
import eu.timepit.refined.types.string.NonEmptyString
import skunk._
import skunk.codec.all._
import skunk.data.Type
import squants.Money

package object sql {
  def identity[A: IsUUID]: Codec[A] = uuid.imap[A](IsUUID[A].uuid.get)(IsUUID[A].uuid.apply)

  val nes: Codec[NonEmptyString] = varchar.imap[NonEmptyString](NonEmptyString.unsafeFrom)(_.value)
  val price: Codec[Money] = numeric.imap[Money](money => UZS(money))(_.amount)

  val firstName: Codec[FirstName] = nes.imap[FirstName](FirstName.apply)(_.value)
  val lastName: Codec[LastName] = nes.imap[LastName](LastName.apply)(_.value)
  val address: Codec[Address] = nes.imap[Address](Address.apply)(_.value)
  val role: Codec[Role] = `enum`[Role](_.value, Role.find, Type("role"))

}
