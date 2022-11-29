package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import squants.Money
import babymed.services.visits.domain.types._

@derive(decoder, encoder)
case class Service(
    id: ServiceId,
    serviceTypeId: ServiceTypeId,
    name: ServiceName,
    price: Money,
  )
