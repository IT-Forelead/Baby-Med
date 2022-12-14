package babymed.services.users.domain

import java.time.LocalDate
import java.time.LocalDateTime

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import eu.timepit.refined.types.numeric.NonNegInt
import io.circe.refined._

import babymed.refinements.Phone
import babymed.services.users.domain.types._

@derive(encoder, decoder)
case class PatientFilters(
    startDate: Option[LocalDateTime] = None,
    endDate: Option[LocalDateTime] = None,
    patientFirstName: Option[FirstName] = None,
    patientLastName: Option[LastName] = None,
    regionId: Option[RegionId] = None,
    cityId: Option[CityId] = None,
    address: Option[Address] = None,
    birthday: Option[LocalDate] = None,
    phone: Option[Phone] = None,
    page: Option[NonNegInt] = None,
    limit: Option[NonNegInt] = None,
  )

object PatientFilters {
  val Empty: PatientFilters = PatientFilters()
}
