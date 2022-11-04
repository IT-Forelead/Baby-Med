package babymed.services.users.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import eu.timepit.refined.types.numeric.NonNegInt
import io.circe.refined._

import babymed.domain.Role
import babymed.refinements.Phone
import babymed.services.users.domain.types._

@derive(encoder, decoder)
case class UserFilters(
    firstName: Option[FirstName] = None,
    lastName: Option[LastName] = None,
    role: Option[Role] = None,
    phone: Option[Phone] = None,
    page: Option[NonNegInt] = None,
    limit: Option[NonNegInt] = None,
  )

object UserFilters {
  val Empty: UserFilters = UserFilters()
}
