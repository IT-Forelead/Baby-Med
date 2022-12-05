package babymed.services.babymed.api

import java.util.UUID

import scala.util.Try

import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.QueryParamDecoder
import org.http4s.dsl.impl._

import babymed.services.users.domain.types.Fullname
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.UserId
import babymed.services.visits.domain.types.OperationExpenseId
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.domain.types.ServiceTypeId
import babymed.util.MyPathVar

package object routes {
  implicit val fullNameDecoder: QueryParamDecoder[Fullname] =
    QueryParamDecoder[String].map(str => Fullname(NonEmptyString.unsafeFrom("%" + str + "%")))
  implicit val serviceTypeIdDecoder: QueryParamDecoder[ServiceTypeId] =
    QueryParamDecoder[String].map(str => ServiceTypeId(UUID.fromString(str)))

  object FullNameParam extends QueryParamDecoderMatcher[Fullname]("full_name")
  object ServiceTypeIdParam extends QueryParamDecoderMatcher[ServiceTypeId]("type_Id")
  object UserIdVar extends MyPathVar(str => Try(UserId(UUID.fromString(str))))
  object RegionIdVar extends MyPathVar(str => Try(RegionId(UUID.fromString(str))))
  object ServiceIdVar extends MyPathVar(str => Try(ServiceId(UUID.fromString(str))))
  object ServiceTypeIdVar extends MyPathVar(str => Try(ServiceTypeId(UUID.fromString(str))))
  object PatientVisitIdVar extends MyPathVar(str => Try(PatientVisitId(UUID.fromString(str))))
  object OperationExpenseIdVar
      extends MyPathVar(str => Try(OperationExpenseId(UUID.fromString(str))))
  object PatientNameVar
      extends MyPathVar(str => Try(Fullname(NonEmptyString.unsafeFrom('%' + str + '%'))))
}
