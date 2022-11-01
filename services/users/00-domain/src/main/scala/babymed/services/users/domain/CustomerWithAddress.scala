package babymed.services.users.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

@derive(decoder, encoder)
case class CustomerWithAddress(
    customer: Customer,
    region: Region,
    town: Town,
  )
