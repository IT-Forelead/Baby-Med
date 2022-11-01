package babymed.services.payments.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.users.domain.Customer

@derive(decoder, encoder)
case class PaymentWithCustomer(
    payment: Payment,
    customer: Customer,
  )
