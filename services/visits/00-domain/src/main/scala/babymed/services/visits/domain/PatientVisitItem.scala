package babymed.services.visits.domain

import babymed.services.visits.domain.types.{PatientVisitId, ServiceId}
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(decoder, encoder)
case class PatientVisitItem(
    visitId: PatientVisitId,
    serviceId: ServiceId,
  )
