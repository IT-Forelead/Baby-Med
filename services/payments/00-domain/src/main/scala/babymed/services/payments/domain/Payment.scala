package babymed.services.payments.domain

import java.time.LocalDateTime

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import squants.Money

import babymed.services.payments.domain.types._
import babymed.services.users.domain.types.CustomerId

@derive(decoder, encoder)
case class Payment(
    id: PaymentId,
    createdAt: LocalDateTime,
    customerId: CustomerId,
    price: Money,
  )
