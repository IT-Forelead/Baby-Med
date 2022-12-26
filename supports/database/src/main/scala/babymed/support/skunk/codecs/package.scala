package babymed.support.skunk

import eu.timepit.refined.types.string.NonEmptyString
import skunk.Codec
import skunk.codec.all.int4
import skunk.codec.all.uuid
import skunk.codec.all.varchar

import babymed.effects.IsUUID
import babymed.refinements._
import babymed.syntax.refined.commonSyntaxAutoRefineV

package object codecs {
  def identification[A: IsUUID]: Codec[A] = uuid.imap[A](IsUUID[A].uuid.get)(IsUUID[A].uuid.apply)

  val nes: Codec[NonEmptyString] = varchar.imap[NonEmptyString](identity(_))(_.value)
  val phone: Codec[Phone] = varchar.imap[Phone](identity(_))(_.value)
  val percent: Codec[Percent] = int4.imap[Percent](identity(_))(_.value)
  val email: Codec[EmailAddress] = varchar.imap[EmailAddress](identity(_))(_.value)
}
