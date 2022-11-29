package babymed.services.users.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.users.domain.types.SubRoleId
import babymed.services.users.domain.types.SubRoleName

@derive(decoder, encoder)
case class SubRole(
    id: SubRoleId,
    name: SubRoleName,
  )
