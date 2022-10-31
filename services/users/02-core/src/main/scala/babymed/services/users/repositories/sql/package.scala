package babymed.services.users.repositories

import babymed.domain.Role
import babymed.effects.IsUUID
import babymed.services.users.domain.types._
import cats.implicits._
import eu.timepit.refined.types.string.NonEmptyString
import skunk._
import skunk.codec.all._
import skunk.codec.all.int4
import skunk.codec.all.uuid
import skunk.codec.all.varchar
import skunk.data.Type
import skunk.implicits._
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

package object sql {
  def identity[A: IsUUID]: Codec[A] = uuid.imap[A](IsUUID[A].uuid.get)(IsUUID[A].uuid.apply)

  val nes: Codec[NonEmptyString] = varchar.imap[NonEmptyString](NonEmptyString.unsafeFrom)(_.value)

  val firstName: Codec[FirstName] = nes.imap[FirstName](FirstName.apply)(_.value)

  val lastName: Codec[LastName] = nes.imap[LastName](LastName.apply)(_.value)

  val address: Codec[Address] = nes.imap[Address](Address.apply)(_.value)

  val region: Codec[Region] = nes.imap[Region](Region.apply)(_.value)

  val town: Codec[Town] = nes.imap[Town](Town.apply)(_.value)

  val passwordHash: Codec[PasswordHash[SCrypt]] =
    varchar.imap[PasswordHash[SCrypt]](PasswordHash[SCrypt])(_.toString)

  val role: Codec[Role] = `enum`[Role](_.value, Role.find, Type("role"))

  final implicit class FragmentOps(af: AppliedFragment) {
    def paginate(lim: Int, index: Int): AppliedFragment = {
      val offset = (index - 1) * lim
      val filter: Fragment[Int ~ Int] = sql" LIMIT $int4 OFFSET $int4 "
      af |+| filter(lim ~ offset)
    }

    /** Returns `WHERE (f1) AND (f2) AND ... (fn)` for defined `f`, if any, otherwise the empty fragment. */
    def whereAndOpt(fs: List[AppliedFragment]): AppliedFragment = {
      val filters =
        if (fs.isEmpty)
          AppliedFragment.empty
        else
          fs.foldSmash(void" WHERE ", void" AND ", AppliedFragment.empty)
      af |+| filters
    }

    def andOpt(fs: List[AppliedFragment]): AppliedFragment = {
      val filters =
        if (fs.isEmpty)
          AppliedFragment.empty
        else
          fs.foldSmash(void" AND ", void" AND ", AppliedFragment.empty)
      af |+| filters
    }
  }
}
