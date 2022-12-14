package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.users.domain.User

@derive(decoder, encoder)
case class CheckupExpenseInfo(
    checkupExpense: CheckupExpense,
    doctorShare: DoctorShare,
    service: ServiceWithTypeName,
    user: User,
  )
