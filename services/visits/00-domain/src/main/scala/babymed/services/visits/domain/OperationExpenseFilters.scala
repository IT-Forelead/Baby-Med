package babymed.services.visits.domain

import java.time.LocalDateTime

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import eu.timepit.refined.types.numeric.NonNegInt
import io.circe.refined._

@derive(encoder, decoder)
case class OperationExpenseFilters(
    startDate: Option[LocalDateTime] = None,
    endDate: Option[LocalDateTime] = None,
    page: Option[NonNegInt] = None,
    limit: Option[NonNegInt] = None,
  )

object OperationExpenseFilters {
  val Empty: OperationExpenseFilters = OperationExpenseFilters()
}
