package babymed.services.users.domain

import babymed.refinements.Phone
import babymed.services.users.domain.types.{FirstName, LastName, PatientId}
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.circe.refined._


@derive(decoder, encoder)
case class PatientWithName(
    id: PatientId,
    firstName: FirstName,
    lastName: LastName,
    phone: Phone
  )
