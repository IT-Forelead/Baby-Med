package babymed.services.babymed.api.routes

import babymed.exception.AuthError
import babymed.services.auth.impl.Security
import babymed.services.auth.domain.Credentials
import babymed.services.auth.domain.types.tokenCodec
import babymed.services.users.domain.User
import babymed.support.services.syntax.all.http4SyntaxReqOps
import cats.Monad
import cats.MonadThrow
import cats.implicits._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

final case class AuthRoutes[F[_]: Monad: JsonDecoder: MonadThrow](
    security: Security[F]
  )(implicit
    logger: Logger[F]
  ) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/auth"

  private val publicRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "login" =>
        req.decodeR[Credentials] { credentials =>
          security
            .auth
            .login(credentials)
            .flatMap(Ok(_))
            .recoverWith {
              case AuthError.PasswordDoesNotMatch(message) =>
                logger.info(message) >>
                  Forbidden("Incorrect Login or password")
              case AuthError.NoSuchUser(message) =>
                logger.info(message) >>
                  Forbidden("Incorrect Login or password")
            }
        }
    }

  private[this] val privateRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {
    case ar @ GET -> Root / "logout" as user =>
      security.auth.destroySession(ar.req, user.phone) *> NoContent()
  }

  lazy val routes: HttpRoutes[F] = Router(
    prefixPath -> (publicRoutes <+> security
      .auth
      .usersMiddleware(security.userJwtAuth)(privateRoutes))
  )

}
