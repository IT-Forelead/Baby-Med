package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

@derive(decoder, encoder)
case class OperationServiceInfo(
    operationService: OperationService,
    service: ServiceWithTypeName,
  )
