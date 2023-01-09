package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.users.domain.City
import babymed.services.users.domain.Patient
import babymed.services.users.domain.Region

@derive(encoder, decoder)
case class OperationInfo(
    operation: Operation,
    service: ServiceWithTypeName,
    patient: Patient,
    region: Region,
    city: City,
  )
