package babymed.services.payments.repositories

import babymed.effects.IsUUID
import babymed.services.payments.domain.types.UZS
import eu.timepit.refined.types.string.NonEmptyString
import skunk._
import skunk.codec.all._
import squants.Money

package object sql {
  def identity[A: IsUUID]: Codec[A] = uuid.imap[A](IsUUID[A].uuid.get)(IsUUID[A].uuid.apply)

  val nes: Codec[NonEmptyString] = varchar.imap[NonEmptyString](NonEmptyString.unsafeFrom)(_.value)
  val price: Codec[Money] = numeric.imap[Money](money => UZS(money))(_.amount)

}
