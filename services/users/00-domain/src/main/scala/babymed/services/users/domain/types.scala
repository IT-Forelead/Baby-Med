package babymed.services.users.domain

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
  @newtype case class UserId(value: UUID)
  @derive(decoder, encoder, eqv, show, uuid)
  @newtype case class CustomerId(value: UUID)
  @derive(decoder, encoder, eqv, show, uuid)
  @newtype case class RegionId(value: UUID)
  @derive(decoder, encoder, eqv, show, uuid)
  @newtype case class TownId(value: UUID)
  @derive(decoder, encoder, eqv)
  @newtype case class FirstName(value: NonEmptyString)
  @derive(decoder, encoder, eqv)
  @newtype case class LastName(value: NonEmptyString)
  @derive(decoder, encoder, eqv)
  @newtype case class Address(value: NonEmptyString)
  @derive(decoder, encoder, eqv)
  @newtype case class RegionName(value: NonEmptyString)
  @derive(decoder, encoder, eqv)
  @newtype case class TownName(value: NonEmptyString)
}
