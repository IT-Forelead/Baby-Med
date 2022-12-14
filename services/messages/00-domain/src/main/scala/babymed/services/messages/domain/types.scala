package babymed.services.messages.domain

import java.util.UUID

import derevo.cats.eqv
import derevo.cats.show
import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import eu.timepit.refined.cats._
import eu.timepit.refined.types.all.NonEmptyString
import io.circe.refined._
import io.estatico.newtype.macros.newtype

import babymed.effects.uuid

object types {
  @derive(decoder, encoder, eqv, show, uuid)
  @newtype case class MessageId(value: UUID)
  @derive(decoder, encoder, eqv)
  @newtype case class MessageText(value: NonEmptyString)
}
