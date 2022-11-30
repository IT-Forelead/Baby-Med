package babymed.services.babymed.api

import java.time.LocalDateTime
import java.util.UUID

import scala.util.Try

import eu.timepit.refined.types.numeric.NonNegInt
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.dsl.impl._

import babymed.services.users.domain.types.Fullname
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.UserId
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.domain.types.ServiceTypeId
import babymed.support.services.syntax.http4s._
import babymed.syntax.refined.commonSyntaxAutoRefineV
import babymed.util.MyPathVar

package object routes {
  object name extends OptionalQueryParamDecoderMatcher[String]("fullname")
  object UserIdVar extends MyPathVar(str => Try(UserId(UUID.fromString(str))))
  object RegionIdVar extends MyPathVar(str => Try(RegionId(UUID.fromString(str))))
  object ServiceIdVar extends MyPathVar(str => Try(ServiceId(UUID.fromString(str))))
  object ServiceTypeIdVar extends MyPathVar(str => Try(ServiceTypeId(UUID.fromString(str))))
  object PatientVisitIdVar extends MyPathVar(str => Try(PatientVisitId(UUID.fromString(str))))
  object PatientNameVar
      extends MyPathVar(str => Try(Fullname(NonEmptyString.unsafeFrom('%' + str + '%'))))
}
