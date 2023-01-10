package babymed.services.visits.domain

import java.time.LocalDateTime

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.users.domain.types.PatientId
import babymed.services.visits.domain.types.OperationId
import babymed.services.visits.domain.types.ServiceId

@derive(decoder, encoder)
case class Operation(
    id: OperationId,
    createdAt: LocalDateTime,
    patientId: PatientId,
    serviceId: ServiceId,
  )
