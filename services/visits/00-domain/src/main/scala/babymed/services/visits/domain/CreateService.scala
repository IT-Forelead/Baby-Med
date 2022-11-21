package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import squants.Money

import babymed.services.visits.domain.types.ServiceName

@derive(decoder, encoder)
case class CreateService(
    name: ServiceName,
    cost: Money,
  )
