package babymed.integrations.opersms.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import eu.timepit.refined.types.string.NonEmptyString

import babymed.refinements.Phone
import babymed.syntax.refined.commonSyntaxAutoUnwrapV

@derive(decoder, encoder)
case class SMS(
    phone: String,
    text: String,
  )

object SMS {
  def unPlus(phone: Phone, text: NonEmptyString): SMS =
    SMS(phone.value.replace("+", ""), text)
}
