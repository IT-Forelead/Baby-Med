package babymed.services.users.repositories

import eu.timepit.refined.types.string.NonEmptyString
import skunk._
import skunk.codec.all.uuid
import skunk.codec.all.varchar
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

import babymed.domain.Role
import babymed.effects.IsUUID
import babymed.services.users.domain.types._

package object sql {
  def identity[A: IsUUID]: Codec[A] = uuid.imap[A](IsUUID[A].uuid.get)(IsUUID[A].uuid.apply)

  val nes: Codec[NonEmptyString] = varchar.imap[NonEmptyString](NonEmptyString.unsafeFrom)(_.value)
  val userId: Codec[UserId] = identity[UserId]
  val patientId: Codec[PatientId] = identity[PatientId]
  val regionId: Codec[RegionId] = identity[RegionId]
  val cityId: Codec[CityId] = identity[CityId]
  val subRoleId: Codec[SubRoleId] = identity[SubRoleId]
  val firstName: Codec[FirstName] = nes.imap[FirstName](FirstName.apply)(_.value)
  val lastName: Codec[LastName] = nes.imap[LastName](LastName.apply)(_.value)
  val fullName: Codec[Fullname] = nes.imap[Fullname](Fullname.apply)(_.value)
  val address: Codec[Address] = nes.imap[Address](Address.apply)(_.value)
  val passwordHash: Codec[PasswordHash[SCrypt]] =
    varchar.imap[PasswordHash[SCrypt]](PasswordHash[SCrypt])(_.toString)
  val role: Codec[Role] =
    varchar.eimap[Role](str => Role.values.find(_.value == str).toRight("type not found "))(_.value)
  val regionName: Codec[RegionName] = nes.imap[RegionName](RegionName.apply)(_.value)
  val cityName: Codec[CityName] = nes.imap[CityName](CityName.apply)(_.value)
  val subRoleName: Codec[SubRoleName] = nes.imap[SubRoleName](SubRoleName.apply)(_.value)
}
