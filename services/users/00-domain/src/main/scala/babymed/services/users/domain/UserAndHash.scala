package babymed.services.users.domain

import babymed.Password
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

@derive(decoder, encoder)
case class UserAndHash(user: User, hash: PasswordHash[SCrypt])
