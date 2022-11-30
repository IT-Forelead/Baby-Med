package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import squants.Money

import babymed.services.visits.domain.types.PartnerDoctorFullName
import babymed.services.visits.domain.types.PatientVisitId

@derive(decoder, encoder)
case class CreateOperationExpense(
    patientVisitId: PatientVisitId,
    operationExpenseItems: List[CreateOperationExpenseItem],
    forLaboratory: Money,
    forTools: Money,
    forDrugs: Money,
    partnerDoctorFullName: Option[PartnerDoctorFullName],
    partnerDoctorPrice: Option[Money],
  )
