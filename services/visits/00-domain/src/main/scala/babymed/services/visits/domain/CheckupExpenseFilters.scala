package babymed.services.visits.domain

import java.time.LocalDateTime

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import eu.timepit.refined.types.numeric.NonNegInt
import io.circe.refined._

import babymed.services.users.domain.types.UserId
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.domain.types.ServiceId

@derive(encoder, decoder)
case class CheckupExpenseFilters(
    startDate: Option[LocalDateTime] = None,
    endDate: Option[LocalDateTime] = None,
    patientVisitId: Option[PatientVisitId] = None,
    serviceId: Option[ServiceId] = None,
    userId: Option[UserId] = None,
    page: Option[NonNegInt] = None,
    limit: Option[NonNegInt] = None,
  )

object CheckupExpenseFilters {
  val Empty: CheckupExpenseFilters = CheckupExpenseFilters()
}
