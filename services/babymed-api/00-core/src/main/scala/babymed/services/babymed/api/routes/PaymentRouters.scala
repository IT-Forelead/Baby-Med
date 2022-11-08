package babymed.services.babymed.api.routes

import cats.effect.Async
import cats.implicits._
import org.http4s._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

import babymed.domain.Role._
import babymed.services.auth.impl.Security
import babymed.services.payments.domain.CreatePayment
import babymed.services.payments.domain.PaymentFilters
import babymed.services.payments.proto.Payments
import babymed.services.users.domain.User
import babymed.support.services.syntax.all._

final case class PaymentRouters[F[_]: Async: JsonDecoder](
    security: Security[F],
    payments: Payments[F],
  )(implicit
    logger: Logger[F]
  ) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/payment"

  private[this] val privateRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {

    case ar @ POST -> Root as user if user.role != Doctor =>
      ar.req.decodeR[CreatePayment] { createPayment =>
        payments.create(createPayment) *> NoContent()
      }

    case ar @ POST -> Root / "report" :? page(index) +& limit(limit) as _ =>
      ar.req.decodeR[PaymentFilters] { req =>
        payments
          .get(
            PaymentFilters(
              req.startDate,
              req.endDate,
              page = Some(index),
              limit = Some(limit),
            )
          )
          .flatMap(Ok(_))
      }

    case ar @ POST -> Root / "report" / "summary" as _ =>
      ar.req.decodeR[PaymentFilters] { req =>
        payments.getPaymentsTotal(PaymentFilters(req.startDate, req.endDate)).flatMap(Ok(_))
      }

    case GET -> Root / "delete" / PaymentIdVar(paymentId) as user if user.role == SuperManager =>
      payments.delete(paymentId) >> NoContent()
  }

  lazy val routes: HttpRoutes[F] = Router(
    prefixPath -> security.auth.usersMiddleware(security.userJwtAuth)(privateRoutes)
  )
}
