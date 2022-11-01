package babymed.services.payments.domain

import java.time.LocalDateTime

import babymed.services.payments.domain.types._
import babymed.services.users.domain.types.CustomerId
import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import squants.Money

@derive(decoder, encoder)
case class Payment(
    id: PaymentId,
    createdAt: LocalDateTime,
    customerId: CustomerId,
    price: Money,
  )
