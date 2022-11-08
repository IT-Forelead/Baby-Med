package babymed.services.babymed.api.routes

import cats.effect.Async
import cats.implicits._
import eu.timepit.refined.types.numeric.NonNegInt
import org.http4s._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

import babymed.domain.Role.Doctor
import babymed.services.auth.impl.Security
import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.SearchFilters
import babymed.services.users.domain.User
import babymed.services.users.proto.Customers
import babymed.support.services.syntax.all._

final case class CustomerRouters[F[_]: Async: JsonDecoder](
    security: Security[F],
    customers: Customers[F],
  )(implicit
    logger: Logger[F]
  ) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/customer"

  private[this] val privateRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {

    case ar @ POST -> Root as user if user.role != Doctor =>
      ar.req.decodeR[CreateCustomer] { createCustomer =>
        customers.createCustomers(createCustomer) *> NoContent()
      }

    case ar @ POST -> Root / "report" :? page(index) +& limit(limit) as _ =>
      ar.req.decodeR[SearchFilters] { req =>
        customers
          .getCustomers(
            SearchFilters(
              req.startDate,
              req.endDate,
              page = Some(NonNegInt.unsafeFrom(index)),
              limit = Some(NonNegInt.unsafeFrom(limit)),
            )
          )
          .flatMap(Ok(_))
      }

    case ar @ POST -> Root / "report" / "summary" as _ =>
      ar.req.decodeR[SearchFilters] { req =>
        customers.getTotalCustomers(SearchFilters(req.startDate, req.endDate)).flatMap(Ok(_))
      }

    case GET -> Root / "regions" as _ =>
      customers.getRegions.flatMap(Ok(_))

    case GET -> Root / "towns" / RegionIdVar(regionId) as _ =>
      customers.getTownsByRegionId(regionId).flatMap(Ok(_))
  }

  lazy val routes: HttpRoutes[F] = Router(
    prefixPath -> security.auth.usersMiddleware(security.userJwtAuth)(privateRoutes)
  )
}
