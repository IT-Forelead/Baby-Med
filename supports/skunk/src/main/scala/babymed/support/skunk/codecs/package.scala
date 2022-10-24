package babymed.support.skunk

import babymed.EmailAddress
import babymed.OnlyDigits
import babymed.effects.IsUUID
import babymed.syntax.refined.commonSyntaxAutoRefineV
import eu.timepit.refined.types.string.NonEmptyString
import skunk.Codec
import skunk.codec.all.{timestamptz, uuid, varchar}

import java.time.ZonedDateTime

package object codecs {
  def identification[A: IsUUID]: Codec[A] = uuid.imap[A](IsUUID[A].uuid.get)(IsUUID[A].uuid.apply)

  val nes: Codec[NonEmptyString] = varchar.imap[NonEmptyString](identity(_))(_.value)
  val number: Codec[OnlyDigits] = varchar.imap[OnlyDigits](identity(_))(_.value)
  val email: Codec[EmailAddress] = varchar.imap[EmailAddress](identity(_))(_.value)
  val zonedDateTime: Codec[ZonedDateTime] = timestamptz.imap(_.toZonedDateTime)(_.toOffsetDateTime)
}
