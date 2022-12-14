package babymed.services.babymed.api.routes

import cats.effect.Async
import cats.implicits._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

import babymed.domain.Role.Cashier
import babymed.domain.Role.SuperManager
import babymed.domain.Role.TechAdmin
import babymed.services.auth.impl.Security
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.EditUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserFilters
import babymed.services.users.proto.Users
import babymed.support.services.syntax.all.http4SyntaxReqOps

final case class UserRouters[F[_]: Async: JsonDecoder](
    security: Security[F],
    users: Users[F],
  )(implicit
    logger: Logger[F]
  ) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/user"

  private[this] val privateRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {

    case ar @ POST -> Root as user if List(SuperManager, TechAdmin).contains(user.role) =>
      ar.req.decodeR[CreateUser] { createUser =>
        users.validationAndCreate(createUser) *> NoContent()
      }

    case ar @ POST -> Root / "report" as user
         if List(SuperManager, Cashier, TechAdmin).contains(user.role) =>
      ar.req.decodeR[UserFilters] { userFilters =>
        users
          .get(userFilters)
          .flatMap(Ok(_))
      }

    case ar @ POST -> Root / "report" / "summary" as user
         if List(SuperManager, Cashier, TechAdmin).contains(user.role) =>
      ar.req.decodeR[UserFilters] { userFilters =>
        users.getTotal(userFilters).flatMap(Ok(_))
      }

    case ar @ POST -> Root / "update" as user
         if List(SuperManager, TechAdmin).contains(user.role) =>
      ar.req.decodeR[EditUser] { editUser =>
        users.validationAndEdit(editUser) *> NoContent()
      }

    case GET -> Root / "delete" / UserIdVar(userId) as user
         if List(SuperManager, TechAdmin).contains(user.role) =>
      users.delete(userId) >> NoContent()

    case GET -> Root / "sub-roles" as user
         if List(SuperManager, Cashier, TechAdmin).contains(user.role) =>
      users.getSubRoles.flatMap(Ok(_))
  }

  lazy val routes: HttpRoutes[F] = Router(
    prefixPath -> security.auth.usersMiddleware(security.userJwtAuth)(privateRoutes)
  )
}
