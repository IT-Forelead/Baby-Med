package babymed.services.babymed.api

import java.time.LocalDateTime
import java.util.UUID
import scala.util.Try
import eu.timepit.refined.types.numeric.NonNegInt
import org.http4s.dsl.impl._
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.UserId
import babymed.services.visits.domain.types.{PatientVisitId, ServiceId, ServiceTypeId}
import babymed.support.services.syntax.http4s._
import babymed.syntax.refined.commonSyntaxAutoRefineV
import babymed.util.MyPathVar

package object routes {
  object page extends QueryParamDecoderMatcherWithDefault[NonNegInt]("page", 1)
  object limit extends QueryParamDecoderMatcherWithDefault[NonNegInt]("limit", 30)
  object from extends OptionalQueryParamDecoderMatcher[LocalDateTime]("from")
  object to extends OptionalQueryParamDecoderMatcher[LocalDateTime]("to")
  object UserIdVar extends MyPathVar(str => Try(UserId(UUID.fromString(str))))
  object RegionIdVar extends MyPathVar(str => Try(RegionId(UUID.fromString(str))))
  object ServiceIdVar extends MyPathVar(str => Try(ServiceId(UUID.fromString(str))))
  object ServiceTypeIdVar extends MyPathVar(str => Try(ServiceTypeId(UUID.fromString(str))))
  object PatientVisitIdVar extends MyPathVar(str => Try(PatientVisitId(UUID.fromString(str))))
}
