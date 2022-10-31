package babymed.services.payments.domain

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import eu.timepit.refined.types.numeric.NonNegInt
import io.circe.refined._

import java.time.LocalDateTime

@derive(decoder, encoder)
case class SearchFilters (
  startDate: Option[LocalDateTime] = None,
  endDate: Option[LocalDateTime] = None,
  page: Option[NonNegInt] = None,
  limit: Option[NonNegInt] = None
)

object SearchFilters {
  val Empty: SearchFilters = SearchFilters()
}
