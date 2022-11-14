package babymed.services.visits.repositories

import eu.timepit.refined.types.string.NonEmptyString
import skunk._
import skunk.codec.all._
import skunk.data.Type
import squants.Money

import babymed.domain.PaymentStatus
import babymed.effects.IsUUID
import babymed.services.visits.domain.types.ServiceName
import babymed.services.visits.domain.types.UZS

package object sql {
  def identity[A: IsUUID]: Codec[A] = uuid.imap[A](IsUUID[A].uuid.get)(IsUUID[A].uuid.apply)

  val nes: Codec[NonEmptyString] = varchar.imap[NonEmptyString](NonEmptyString.unsafeFrom)(_.value)
  val serviceName: Codec[ServiceName] = nes.imap[ServiceName](ServiceName.apply)(_.value)
  val cost: Codec[Money] = numeric.imap[Money](money => UZS(money))(_.amount)

  val paymentStatus: Codec[PaymentStatus] =
    `enum`[PaymentStatus](_.value, PaymentStatus.find, Type("role"))
}
