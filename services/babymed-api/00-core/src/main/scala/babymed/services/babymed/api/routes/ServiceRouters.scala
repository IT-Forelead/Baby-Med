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
import babymed.services.users.domain.User
import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.EditService
import babymed.services.visits.domain.types.ServiceTypeName
import babymed.services.visits.proto.Services
import babymed.support.services.syntax.all._

final case class ServiceRouters[F[_]: Async: JsonDecoder](
    security: Security[F],
    services: Services[F],
  )(implicit
    logger: Logger[F]
  ) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/service"

  private[this] val privateRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {

    case ar @ POST -> Root / "create" as user if user.role != Doctor =>
      ar.req.decodeR[CreateService] { createService =>
        services.create(createService) *> NoContent()
      }

    case GET -> Root / "services" :? ServiceTypeIdParam(id) as _ =>
      services.getServicesByTypeId(id).flatMap(Ok(_))

    case GET -> Root / "services" as _ =>
      services.get.flatMap(Ok(_))

    case ar @ POST -> Root / "edit" as user if user.role != Doctor =>
      ar.req.decodeR[EditService] { editService =>
        services.edit(editService) *> NoContent()
      }

    case GET -> Root / "delete" / ServiceIdVar(serviceId) as user if user.role != Doctor =>
      services.delete(serviceId) *> NoContent()

    case ar @ POST -> Root / "create" / "service-type" as user if user.role != Doctor =>
      ar.req.decodeR[ServiceTypeName] { serviceTypeName =>
        services.createServiceType(serviceTypeName) *> NoContent()
      }

    case GET -> Root / "service-types" as _ =>
      services.getServiceTypes.flatMap(Ok(_))

    case GET -> Root / "delete-service-type" / ServiceTypeIdVar(serviceTypeId) as user
         if user.role != Doctor =>
      services.deleteServiceType(serviceTypeId) *> NoContent()
  }

  lazy val routes: HttpRoutes[F] = Router(
    prefixPath -> security.auth.usersMiddleware(security.userJwtAuth)(privateRoutes)
  )
}
