package babymed.services.babymed.api.routes

import cats.effect.Async
import cats.implicits._
import org.http4s.AuthedRoutes
import org.http4s.HttpRoutes
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

import babymed.domain.Role.Admin
import babymed.domain.Role.Cashier
import babymed.domain.Role.SuperManager
import babymed.domain.Role.TechAdmin
import babymed.services.auth.impl.Security
import babymed.services.users.domain.User
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.proto.Visits
import babymed.support.services.syntax.all.deriveEntityEncoder
import babymed.support.services.syntax.all.http4SyntaxReqOps

final case class VisitRouters[F[_]: Async: JsonDecoder](
    security: Security[F],
    visits: Visits[F],
  )(implicit
    logger: Logger[F]
  ) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/visit"

  private[this] val privateRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {

    case ar @ POST -> Root / "create" as user
         if List(SuperManager, Admin, TechAdmin).contains(user.role) =>
      ar.req.decodeR[CreatePatientVisit] { createVisit =>
        visits.create(createVisit) *> NoContent()
      }

    case ar @ POST -> Root / "report" as _ =>
      ar.req.decodeR[PatientVisitFilters] { patientVisitFilters =>
        visits
          .get(
            patientVisitFilters
          )
          .flatMap(Ok(_))
      }

    case ar @ POST -> Root / "report" / "summary" as _ =>
      ar.req.decodeR[PatientVisitFilters] { patientVisitFilters =>
        visits.getTotal(patientVisitFilters).flatMap(Ok(_))
      }

    case GET -> Root / "update-payment-status" / ChequeIdVar(chequeId) as user
         if List(SuperManager, Cashier, TechAdmin).contains(user.role) =>
      visits.updatePaymentStatus(chequeId).flatMap(_ => NoContent()).handleErrorWith { _ =>
        NoContent()
      }

  }

  lazy val routes: HttpRoutes[F] = Router(
    prefixPath -> security.auth.usersMiddleware(security.userJwtAuth)(privateRoutes)
  )
}
