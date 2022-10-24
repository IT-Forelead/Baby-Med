package babymed.services.users.domain

import babymed.Password
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(decoder, encoder)
case class UserAndHash(user: User, hash: Password)
