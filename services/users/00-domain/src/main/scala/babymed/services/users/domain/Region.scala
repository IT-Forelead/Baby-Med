package babymed.services.users.domain

import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.RegionName
import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

@derive(decoder, encoder)
case class Region(
    id: RegionId,
    name: RegionName,
  )
