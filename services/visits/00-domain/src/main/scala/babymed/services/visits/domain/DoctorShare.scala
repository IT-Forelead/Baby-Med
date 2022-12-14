package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import io.circe.refined._

import babymed.refinements.Percent
import babymed.services.users.domain.types.UserId
import babymed.services.visits.domain.types.DoctorShareId
import babymed.services.visits.domain.types.ServiceId

@derive(decoder, encoder)
case class DoctorShare(
    id: DoctorShareId,
    serviceId: ServiceId,
    userId: UserId,
    percent: Percent,
  )
