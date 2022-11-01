package babymed.services.users.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.RegionName

@derive(decoder, encoder)
case class Region(
    id: RegionId,
    name: RegionName,
  )
