package babymed.services.users.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.users.domain.types.CityId
import babymed.services.users.domain.types.CityName
import babymed.services.users.domain.types.RegionId

@derive(decoder, encoder)
case class City(
    id: CityId,
    regionId: RegionId,
    name: CityName,
  )
