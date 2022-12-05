package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.visits.domain.types.ServiceTypeId
import babymed.services.visits.domain.types.ServiceTypeName

@derive(decoder, encoder)
case class ServiceType(
    id: ServiceTypeId,
    name: ServiceTypeName,
  )
