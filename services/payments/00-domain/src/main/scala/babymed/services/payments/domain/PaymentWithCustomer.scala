package babymed.services.payments.domain

import babymed.services.users.domain.Customer
import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

@derive(decoder, encoder)
case class PaymentWithCustomer(
    payment: Payment,
    customer: Customer,
  )
