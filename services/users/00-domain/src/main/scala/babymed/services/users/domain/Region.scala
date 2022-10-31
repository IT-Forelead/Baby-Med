package babymed.services.users.domain

import babymed.services.users.domain.types.{RegionId, RegionName}
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(decoder, encoder)
case class Region (
    id: RegionId,
    name: RegionName
)
