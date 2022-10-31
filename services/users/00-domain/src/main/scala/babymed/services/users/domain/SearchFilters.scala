package babymed.services.users.domain

import java.time.LocalDate
import java.time.ZonedDateTime

import babymed.refinements.Phone
import babymed.services.users.domain.types.Address
import babymed.services.users.domain.types.FirstName
import babymed.services.users.domain.types.LastName
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId
import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import eu.timepit.refined.types.numeric.NonNegInt
import io.circe.refined._

@derive(encoder, decoder)
case class SearchFilters(
    startDate: Option[ZonedDateTime] = None,
    endDate: Option[ZonedDateTime] = None,
    customerFirstName: Option[FirstName] = None,
    customerLastName: Option[LastName] = None,
    regionId: Option[RegionId] = None,
    townId: Option[TownId] = None,
    address: Option[Address] = None,
    birthday: Option[LocalDate] = None,
    phone: Option[Phone] = None,
    page: Option[NonNegInt] = None,
    limit: Option[NonNegInt] = None,
  )

object SearchFilters {
  val Empty: SearchFilters = SearchFilters()
}
