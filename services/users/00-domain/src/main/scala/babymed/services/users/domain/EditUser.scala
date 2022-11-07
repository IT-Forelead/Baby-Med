package babymed.services.users.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import io.circe.refined._

import babymed.domain.Role
import babymed.refinements.Phone
import babymed.services.users.domain.types._

@derive(decoder, encoder)
case class EditUser(
    id: UserId,
    firstname: FirstName,
    lastname: LastName,
    phone: Phone,
    role: Role,
  )
