package babymed.services.users.domain

import babymed.Phone
import babymed.domain.Role
import babymed.services.users.domain.types.{FirstName, LastName}
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

import java.util.UUID

@derive(decoder, encoder)
case class User(
  id: UUID,
  firstname: FirstName,
  lastname: LastName,
  phone: Phone,
  role: Role
)
