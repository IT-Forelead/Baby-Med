package babymed.services.visits.domain

import java.time.LocalDateTime

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import eu.timepit.refined.types.numeric.NonNegInt
import io.circe.refined._

import babymed.domain.PaymentStatus
import babymed.services.users.domain.types.PatientId
import babymed.services.users.domain.types.UserId
import babymed.services.visits.domain.types.ServiceId

@derive(encoder, decoder)
case class PatientVisitFilters(
    startDate: Option[LocalDateTime] = None,
    endDate: Option[LocalDateTime] = None,
    patientId: Option[PatientId] = None,
    userId: Option[UserId] = None,
    serviceId: Option[ServiceId] = None,
    paymentStatus: Option[PaymentStatus] = None,
    page: Option[NonNegInt] = None,
    limit: Option[NonNegInt] = None,
  )

object PatientVisitFilters {
  val Empty: PatientVisitFilters = PatientVisitFilters()
}
