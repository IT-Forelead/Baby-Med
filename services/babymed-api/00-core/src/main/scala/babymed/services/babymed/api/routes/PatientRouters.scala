package babymed.services.babymed.api.routes

import cats.effect.Async
import cats.implicits._
import org.http4s._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

import babymed.domain.Role.Admin
import babymed.domain.Role.SuperManager
import babymed.domain.Role.TechAdmin
import babymed.services.auth.impl.Security
import babymed.services.users.domain.CreatePatient
import babymed.services.users.domain.PatientFilters
import babymed.services.users.domain.User
import babymed.services.users.proto.Patients
import babymed.support.services.syntax.all._

final case class PatientRouters[F[_]: Async: JsonDecoder](
    security: Security[F],
    patients: Patients[F],
  )(implicit
    logger: Logger[F]
  ) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/patient"

  private[this] val privateRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {

    case ar @ POST -> Root as user if List(SuperManager, Admin, TechAdmin).contains(user.role) =>
      ar.req.decodeR[CreatePatient] { createPatient =>
        patients.create(createPatient) *> NoContent()
      }

    case GET -> Root / "search" :? FullNameParam(fullName) as _ =>
      patients.getPatientsByName(fullName).flatMap(Ok(_))

    case ar @ POST -> Root / "report" as _ =>
      ar.req.decodeR[PatientFilters] { patientFilters =>
        patients
          .getPatients(patientFilters)
          .flatMap(Ok(_))
      }

    case ar @ POST -> Root / "report" / "summary" as _ =>
      ar.req.decodeR[PatientFilters] { patientFilters =>
        patients.getTotalPatients(patientFilters).flatMap(Ok(_))
      }

    case GET -> Root / "regions" as _ =>
      patients.getRegions.flatMap(Ok(_))

    case GET -> Root / "cities" / RegionIdVar(regionId) as _ =>
      patients.getCitiesByRegionId(regionId).flatMap(Ok(_))
  }

  lazy val routes: HttpRoutes[F] = Router(
    prefixPath -> security.auth.usersMiddleware(security.userJwtAuth)(privateRoutes)
  )
}
