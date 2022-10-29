package babymed.services.users.domain

import babymed.domain.Role
import babymed.refinements.Phone
import babymed.services.users.domain.types._
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.circe.refined._
import eu.timepit.refined.cats._

@derive(decoder, encoder)
case class CreateUser(
  firstname: FirstName,
  lastname: LastName,
  phone: Phone,
  role: Role
)
