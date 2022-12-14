package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import squants.Money

import babymed.services.visits.domain.types.DoctorShareId

@derive(decoder, encoder)
case class CreateCheckupExpense(
    doctorShareId: DoctorShareId,
    price: Money,
  )
