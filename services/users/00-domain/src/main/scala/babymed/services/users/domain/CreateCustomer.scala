package babymed.services.users.domain

import java.time.LocalDate

import babymed.refinements.Phone
import babymed.services.users.domain.types.Address
import babymed.services.users.domain.types.FirstName
import babymed.services.users.domain.types.LastName
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId
import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import io.circe.refined._

@derive(decoder, encoder)
case class CreateCustomer(
    firstname: FirstName,
    lastname: LastName,
    regionId: RegionId,
    townId: TownId,
    address: Address,
    birthday: LocalDate,
    phone: Phone,
  )
