package babymed.services.payments.domain

import babymed.services.users.domain.Customer
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(decoder, encoder)
case class PaymentWithCustomer (
    payment: Payment,
    customer: Customer
)
