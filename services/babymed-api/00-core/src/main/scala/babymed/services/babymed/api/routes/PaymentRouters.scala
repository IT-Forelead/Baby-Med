package babymed.services.babymed.api.routes

import babymed.services.auth.impl.Security
import babymed.services.payments.domain.{CreatePayment, SearchFilters}
import babymed.services.payments.proto.Payments
import babymed.services.users.domain.User
import babymed.support.services.syntax.all._
import cats.effect.Async
import cats.implicits._
import eu.timepit.refined.types.numeric.NonNegInt
import org.http4s._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

final case class PaymentRouters[F[_]: Async: JsonDecoder](
    security: Security[F],
    payments: Payments[F],
  )(implicit
    logger: Logger[F]
  ) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/payment"

  private[this] val privateRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {

    case ar @ POST -> Root as _ =>
      ar.req.decodeR[CreatePayment] { createPayment =>
        payments.create(createPayment) *> NoContent()
      }

    case ar @ POST -> Root / "report" :? page(index) +& limit(limit) as _ =>
      ar.req.decodeR[SearchFilters] { req =>
        payments
          .get(SearchFilters(req.startDate, req.endDate, page = Some(NonNegInt.unsafeFrom(index)), limit = Some(NonNegInt.unsafeFrom(limit))))
          .flatMap(Ok(_))
      }

    case ar @ POST -> Root / "report" / "summary" as _ =>
      ar.req.decodeR[SearchFilters] { req =>
        payments.getPaymentsTotal(SearchFilters(req.startDate, req.endDate)).flatMap(Ok(_))
      }
  }

  lazy val routes: HttpRoutes[F] = Router(
    prefixPath -> security.auth.usersMiddleware(security.userJwtAuth)(privateRoutes)
  )

}
