package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.users.domain.Patient

@derive(decoder, encoder)
case class OperationExpenseWithPatientVisit(
    operationExpense: OperationExpense,
    patientVisit: PatientVisit,
    patient: Patient,
    service: Service,
  )
