package babymed.services.visits.domain

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
import squants.market.Currency

import babymed.effects.uuid

object types {
  object UZS extends Currency("UZS", "Uzbek sum", "SUM", 2)

  @derive(decoder, encoder, eqv, show, uuid)
  @newtype case class PatientVisitId(value: UUID)
  @derive(decoder, encoder, eqv, show, uuid)
  @newtype case class ServiceId(value: UUID)
  @derive(decoder, encoder, eqv, show, uuid)
  @newtype case class ServiceTypeId(value: UUID)
  @derive(decoder, encoder, eqv, show, uuid)
  @newtype case class OperationExpenseId(value: UUID)
  @derive(decoder, encoder, eqv)
  @newtype case class ServiceName(value: NonEmptyString)
  @derive(decoder, encoder, eqv)
  @newtype case class ServiceTypeName(value: NonEmptyString)
  @derive(decoder, encoder, eqv)
  @newtype case class PartnerDoctorFullName(value: NonEmptyString)
}
