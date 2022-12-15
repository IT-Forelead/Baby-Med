package babymed.services.babymed.api.routes

import cats.effect.Async
import cats.implicits._
import org.http4s.AuthedRoutes
import org.http4s.HttpRoutes
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

import babymed.domain.Role.Doctor
import babymed.services.auth.impl.Security
import babymed.services.users.domain.User
import babymed.services.visits.domain.CheckupExpenseFilters
import babymed.services.visits.domain.CreateDoctorShare
import babymed.services.visits.proto.CheckupExpenses
import babymed.support.services.syntax.all.deriveEntityEncoder
import babymed.support.services.syntax.all.http4SyntaxReqOps

final case class CheckupExpenseRouters[F[_]: Async: JsonDecoder](
    security: Security[F],
    checkupExpenses: CheckupExpenses[F],
  )(implicit
    logger: Logger[F]
  ) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/checkup-expense"

  private[this] val privateRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {

    case ar @ POST -> Root / "create" / "doctor-share" as user if user.role != Doctor =>
      ar.req.decodeR[CreateDoctorShare] { doctorShare =>
        checkupExpenses.createDoctorShare(doctorShare) *> NoContent()
      }

    case ar @ POST -> Root / "report" as _ =>
      ar.req.decodeR[CheckupExpenseFilters] { filters =>
        checkupExpenses.get(filters).flatMap(Ok(_))
      }

    case ar @ POST -> Root / "report" / "summary" as _ =>
      ar.req.decodeR[CheckupExpenseFilters] { filters =>
        checkupExpenses.getTotal(filters).flatMap(Ok(_))
      }

    case GET -> Root / "doctor-shares" as _ =>
      checkupExpenses.getDoctorShares.flatMap(Ok(_))

  }

  lazy val routes: HttpRoutes[F] = Router(
    prefixPath -> security.auth.usersMiddleware(security.userJwtAuth)(privateRoutes)
  )
}
