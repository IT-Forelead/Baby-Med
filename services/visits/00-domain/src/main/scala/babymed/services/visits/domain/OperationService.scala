package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.visits.domain.types._

@derive(decoder, encoder)
case class OperationService(
    id: OperationServiceId,
    serviceId: ServiceId,
  )
