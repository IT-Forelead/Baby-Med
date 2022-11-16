package babymed.services.visits.repositories

import eu.timepit.refined.types.string.NonEmptyString
import skunk._
import skunk.codec.all._
import skunk.data.Type
import squants.Money

import babymed.domain.PaymentStatus
import babymed.domain.Role
import babymed.effects.IsUUID
import babymed.services.users.domain.types._
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.domain.types.ServiceName
import babymed.services.visits.domain.types.UZS

package object sql {
  def identity[A: IsUUID]: Codec[A] = uuid.imap[A](IsUUID[A].uuid.get)(IsUUID[A].uuid.apply)

  val nes: Codec[NonEmptyString] = varchar.imap[NonEmptyString](NonEmptyString.unsafeFrom)(_.value)
  val patientVisitId: Codec[PatientVisitId] = identity[PatientVisitId]
  val userId: Codec[UserId] = identity[UserId]
  val patientId: Codec[PatientId] = identity[PatientId]
  val serviceId: Codec[ServiceId] = identity[ServiceId]
  val regionId: Codec[RegionId] = identity[RegionId]
  val townId: Codec[TownId] = identity[TownId]
  val serviceName: Codec[ServiceName] = nes.imap[ServiceName](ServiceName.apply)(_.value)
  val cost: Codec[Money] = numeric.imap[Money](money => UZS(money))(_.amount)
  val paymentStatus: Codec[PaymentStatus] =
    varchar.eimap[PaymentStatus](str =>
      PaymentStatus.values.find(_.value == str).toRight("type not found ")
    )(_.value)
  val firstName: Codec[FirstName] = nes.imap[FirstName](FirstName.apply)(_.value)
  val lastName: Codec[LastName] = nes.imap[LastName](LastName.apply)(_.value)
  val address: Codec[Address] = nes.imap[Address](Address.apply)(_.value)
  val role: Codec[Role] =
    varchar.eimap[Role](str => Role.values.find(_.value == str).toRight("type not found "))(_.value)
  val regionName: Codec[RegionName] = nes.imap[RegionName](RegionName.apply)(_.value)
  val townName: Codec[TownName] = nes.imap[TownName](TownName.apply)(_.value)
}
