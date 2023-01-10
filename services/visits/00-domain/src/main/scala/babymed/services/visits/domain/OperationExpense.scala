package babymed.services.visits.domain

import java.time.LocalDateTime

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import squants.Money

import babymed.services.visits.domain.types.OperationExpenseId
import babymed.services.visits.domain.types.OperationId
import babymed.services.visits.domain.types.PartnerDoctorFullName

@derive(decoder, encoder)
case class OperationExpense(
    id: OperationExpenseId,
    createdAt: LocalDateTime,
    operationId: OperationId,
    forLaboratory: Money,
    forTools: Money,
    forDrugs: Money,
    partnerDoctorFullName: Option[PartnerDoctorFullName],
    partnerDoctorPrice: Option[Money],
  )
