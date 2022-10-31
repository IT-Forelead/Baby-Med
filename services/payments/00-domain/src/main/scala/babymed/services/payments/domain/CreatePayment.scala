package babymed.services.payments.domain

import babymed.services.users.domain.types.CustomerId
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import squants.Money
import io.circe.refined._

@derive(decoder, encoder)
case class CreatePayment (
  customerId: CustomerId,
  price: Money
)
