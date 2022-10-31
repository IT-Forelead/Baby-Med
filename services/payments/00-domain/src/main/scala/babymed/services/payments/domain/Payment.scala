package babymed.services.payments.domain

import babymed.services.payments.domain.types._
import babymed.services.users.domain.types.CustomerId
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import squants.Money

import java.time.LocalDateTime

@derive(decoder, encoder)
case class Payment (
  id: PaymentId,
  createdAt: LocalDateTime,
  customerId: CustomerId,
  price: Money
)
