package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import squants.Money

import babymed.services.visits.domain.types.OperationId
import babymed.services.visits.domain.types.PartnerDoctorFullName

@derive(decoder, encoder)
case class CreateOperationExpense(
    operationId: OperationId,
    operationExpenseItems: List[CreateOperationExpenseItem],
    forLaboratory: Money,
    forTools: Money,
    forDrugs: Money,
    partnerDoctorFullName: Option[PartnerDoctorFullName],
    partnerDoctorPrice: Option[Money],
  )
