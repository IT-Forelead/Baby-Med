package babymed.services.users.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.users.domain.types.UserId

@derive(decoder, encoder)
case class UserIdWithDeletedStatus(
    id: UserId,
    deleted: Boolean,
  )
