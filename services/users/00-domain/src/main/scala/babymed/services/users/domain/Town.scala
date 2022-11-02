package babymed.services.users.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId
import babymed.services.users.domain.types.TownName

@derive(decoder, encoder)
case class Town(
    id: TownId,
    regionId: RegionId,
    name: TownName,
  )
