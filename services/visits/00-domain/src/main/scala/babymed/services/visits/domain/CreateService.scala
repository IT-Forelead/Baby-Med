package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import squants.Money

import babymed.services.visits.domain.types.ServiceName
import babymed.services.visits.domain.types.ServiceTypeId

@derive(decoder, encoder)
case class CreateService(
    serviceTypeId: ServiceTypeId,
    name: ServiceName,
    price: Money,
  )
