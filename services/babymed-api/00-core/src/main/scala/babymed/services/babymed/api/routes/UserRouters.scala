package babymed.services.babymed.api.routes

import cats.effect.Async
import cats.implicits._
import eu.timepit.refined.types.numeric.NonNegInt
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger
import babymed.domain.Role.SuperManager
import babymed.services.auth.impl.Security
import babymed.services.users.domain.{CreateUser, EditUser, User, UserFilters}
import babymed.services.users.domain.types.UserId
import babymed.services.users.proto.Users
import babymed.support.services.syntax.all.http4SyntaxReqOps

final case class UserRouters[F[_]: Async: JsonDecoder](
    security: Security[F],
    users: Users[F],
  )(implicit
    logger: Logger[F]
  ) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/users"

  private[this] val privateRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {

    case ar @ POST -> Root / "report" :? page(index) +& limit(limit) as user
         if user.role == SuperManager =>
      ar.req.decodeR[UserFilters] { _ =>
        users
          .get(
            UserFilters(
              page = Some(NonNegInt.unsafeFrom(index)),
              limit = Some(NonNegInt.unsafeFrom(limit)),
            )
          )
          .flatMap(Ok(_))
      }

    case ar @ POST -> Root as user if user.role == SuperManager =>
      ar.req.decodeR[CreateUser] { createUser =>
        users.validationAndCreate(createUser) *> NoContent()
      }

    case ar @ POST -> Root / "update" as user if user.role == SuperManager =>
      ar.req.decodeR[EditUser] { editUser =>
        users.validationAndEdit(editUser) *> NoContent()
      }

  }

  lazy val routes: HttpRoutes[F] = Router(
    prefixPath -> security.auth.usersMiddleware(security.userJwtAuth)(privateRoutes)
  )
}
