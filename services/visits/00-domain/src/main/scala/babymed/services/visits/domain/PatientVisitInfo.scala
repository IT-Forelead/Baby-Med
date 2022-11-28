package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.users.domain.City
import babymed.services.users.domain.Patient
import babymed.services.users.domain.Region
import babymed.services.users.domain.User

@derive(encoder, decoder)
case class PatientVisitInfo(
    patientVisit: PatientVisit,
    patient: Patient,
    user: User,
    service: Service,
    region: Region,
    town: City,
  )
