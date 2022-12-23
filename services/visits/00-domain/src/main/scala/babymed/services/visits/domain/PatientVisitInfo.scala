package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import io.circe.refined._

import babymed.services.users.domain.City
import babymed.services.users.domain.Patient
import babymed.services.users.domain.Region
import babymed.services.users.domain.types.FirstName
import babymed.services.users.domain.types.LastName

@derive(encoder, decoder)
case class PatientVisitInfo(
    patientVisit: PatientVisit,
    userFirstName: FirstName,
    userLastName: LastName,
    patient: Patient,
    service: ServiceWithTypeName,
    region: Region,
    city: City,
  )
