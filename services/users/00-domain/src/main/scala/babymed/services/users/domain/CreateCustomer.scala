package babymed.services.users.domain

import babymed.refinements.Phone
import babymed.services.users.domain.types.{Address, FirstName, LastName, RegionId, TownId}
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.circe.refined._

import java.time.LocalDate

@derive(decoder, encoder)
case class CreateCustomer (
  firstname: FirstName,
  lastname: LastName,
  regionId: RegionId,
  townId: TownId,
  address: Address,
  birthday: LocalDate,
  phone: Phone
)
