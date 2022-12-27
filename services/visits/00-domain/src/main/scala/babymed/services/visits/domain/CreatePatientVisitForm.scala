package babymed.services.visits.domain

import babymed.services.users.domain.types.{PatientId, UserId}
import babymed.services.visits.domain.types.ServiceId
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(decoder, encoder)
case class CreatePatientVisitForm(
    patientId: PatientId,
    serviceIds: List[ServiceId],
  )
