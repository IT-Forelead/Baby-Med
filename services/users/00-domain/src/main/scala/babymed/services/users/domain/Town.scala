package babymed.services.users.domain

import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId
import babymed.services.users.domain.types.TownName
import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

@derive(decoder, encoder)
case class Town(
    id: TownId,
    regionId: RegionId,
    name: TownName,
  )
