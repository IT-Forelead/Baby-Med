package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.users.domain.types.PatientId
import babymed.services.visits.domain.types.ServiceId

@derive(decoder, encoder)
case class CreatePatientVisitForm(
    patientId: PatientId,
    serviceIds: List[ServiceId],
  )
