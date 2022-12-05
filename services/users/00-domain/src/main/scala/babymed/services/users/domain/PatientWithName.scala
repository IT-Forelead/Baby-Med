package babymed.services.users.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import io.circe.refined._

import babymed.refinements.Phone
import babymed.services.users.domain.types.FirstName
import babymed.services.users.domain.types.LastName
import babymed.services.users.domain.types.PatientId

@derive(decoder, encoder)
case class PatientWithName(
    id: PatientId,
    firstName: FirstName,
    lastName: LastName,
    phone: Phone,
  )
