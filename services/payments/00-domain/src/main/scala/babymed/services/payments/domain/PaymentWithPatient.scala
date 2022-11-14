package babymed.services.payments.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.users.domain.Patient

@derive(decoder, encoder)
case class PaymentWithPatient(
    payment: Payment,
    patient: Patient,
  )
