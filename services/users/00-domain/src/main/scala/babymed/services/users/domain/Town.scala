package babymed.services.users.domain

import babymed.services.users.domain.types.{RegionId, TownId, TownName}
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(decoder, encoder)
case class Town (
  id: TownId,
  regionId: RegionId,
  name: TownName
)
