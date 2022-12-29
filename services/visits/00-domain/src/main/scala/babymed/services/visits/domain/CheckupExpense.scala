package babymed.services.visits.domain

import java.time.LocalDateTime

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import squants.Money

import babymed.services.visits.domain.types.CheckupExpenseId
import babymed.services.visits.domain.types.DoctorShareId
import babymed.services.visits.domain.types.PatientVisitId

@derive(decoder, encoder)
case class CheckupExpense(
    id: CheckupExpenseId,
    createdAt: LocalDateTime,
    doctorShareId: DoctorShareId,
    patientVisitId: PatientVisitId,
    price: Money,
  )
