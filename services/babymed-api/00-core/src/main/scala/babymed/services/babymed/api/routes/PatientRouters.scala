package babymed.services.babymed.api.routes

import cats.effect.Async
import cats.implicits._
import org.http4s._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

import babymed.domain.Role.Doctor
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

    case ar @ POST -> Root as user if user.role != Doctor =>
      ar.req.decodeR[CreatePatient] { createPatient =>
        patients.create(createPatient) *> NoContent()
      }

    case ar @ POST -> Root / "report" :? page(index) +& limit(limit) as _ =>
      ar.req.decodeR[PatientFilters] { req =>
        patients
          .getPatients(
            PatientFilters(
              req.startDate,
              req.endDate,
              page = Some(index),
              limit = Some(limit),
            )
          )
          .flatMap(Ok(_))
      }

    case ar @ POST -> Root / "report" / "summary" as _ =>
      ar.req.decodeR[PatientFilters] { req =>
        patients.getTotalPatients(PatientFilters(req.startDate, req.endDate)).flatMap(Ok(_))
      }

    case GET -> Root / "regions" as _ =>
      patients.getRegions.flatMap(Ok(_))

    case GET -> Root / "towns" / RegionIdVar(regionId) as _ =>
      patients.getTownsByRegionId(regionId).flatMap(Ok(_))
  }

  lazy val routes: HttpRoutes[F] = Router(
    prefixPath -> security.auth.usersMiddleware(security.userJwtAuth)(privateRoutes)
  )
}
