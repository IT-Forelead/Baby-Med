package babymed.services.payments.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

@derive(decoder, encoder)
case class PaymentsWithTotal(
    payments: List[PaymentWithCustomer],
    total: Long,
  )
