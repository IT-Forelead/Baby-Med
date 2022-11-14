package babymed.services.users.domain

import java.time.LocalDate
import java.time.LocalDateTime

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import eu.timepit.refined.types.numeric.NonNegInt
import io.circe.refined._

import babymed.refinements.Phone
import babymed.services.users.domain.types.Address
import babymed.services.users.domain.types.FirstName
import babymed.services.users.domain.types.LastName
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId

@derive(encoder, decoder)
case class PatientFilters(
    startDate: Option[LocalDateTime] = None,
    endDate: Option[LocalDateTime] = None,
    patientFirstName: Option[FirstName] = None,
    patientLastName: Option[LastName] = None,
    regionId: Option[RegionId] = None,
    townId: Option[TownId] = None,
    address: Option[Address] = None,
    birthday: Option[LocalDate] = None,
    phone: Option[Phone] = None,
    page: Option[NonNegInt] = None,
    limit: Option[NonNegInt] = None,
  )

object PatientFilters {
  val Empty: PatientFilters = PatientFilters()
}
