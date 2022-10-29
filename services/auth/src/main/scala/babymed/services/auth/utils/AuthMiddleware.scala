package babymed.services.auth.utils

import cats.MonadThrow
import cats.data.EitherT
import cats.data.Kleisli
import cats.data.OptionT
import cats.syntax.all._
import dev.profunktor.auth.jwt._
import org.http4s.Credentials.Token
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Authorization
import org.http4s.server
import org.typelevel.ci.CIStringSyntax
import pdi.jwt._

object AuthMiddleware {
  case class AuthData(claim: JwtClaim, token: JwtToken)

  def apply[F[_]: MonadThrow, A](
      jwtAuth: JwtSymmetricAuth,
      authenticate: JwtToken => F[Option[A]],
      maybeProlongToken: AuthData => F[Option[JwtToken]],
      removeToken: JwtToken => F[Unit],
    ): server.AuthMiddleware[F, A] = { routes: AuthedRoutes[A, F] =>
    val dsl = new Http4sDsl[F] {}; import dsl._

    val onFailure: AuthedRoutes[String, F] =
      Kleisli(req => OptionT.liftF(Forbidden(req.context)))

    def getBearerToken: Kleisli[Option, Request[F], JwtToken] =
      Kleisli { request =>
        request
          .headers
          .get[Authorization]
          .collect {
            case Authorization(Token(AuthScheme.Bearer, token)) => JwtToken(token)
          }
          .orElse {
            request.params.get("x-token").map(JwtToken.apply)
          }
      }

    val authData: Kleisli[F, Request[F], Either[String, AuthData]] =
      Kleisli { request =>
        EitherT
          .fromOption[F](getBearerToken(request), "Bearer token not found")
          .flatMapF { token =>
            Jwt
              .decode(
                token.value,
                jwtAuth.secretKey.value,
                jwtAuth.jwtAlgorithms,
                JwtOptions(leeway = 300),
              )
              .liftTo
              .map(AuthData(_, token).asRight[String])
              .handleErrorWith { _ =>
                removeToken(token).as {
                  "Invalid access token".asLeft[AuthData]
                }
              }
          }
          .value
      }

    def authUser(auth: AuthData): F[Either[String, (A, AuthData)]] =
      OptionT(authenticate(auth.token))
        .cata("Invalid token".asLeft[(A, AuthData)], a => (a, auth).asRight[String])

    Kleisli { (req: Request[F]) =>
      OptionT {
        EitherT(authData(req))
          .flatMapF(authUser)
          .foldF(
            err => onFailure(AuthedRequest(err, req)).value,
            {
              case (user, auth) =>
                routes(AuthedRequest(user, req)).semiflatMap { res =>
                  OptionT(maybeProlongToken(auth))
                    .cata(
                      res,
                      token =>
                        res.putHeaders(
                          Header
                            .Raw(ci"Access-Control-Expose-Headers", "X-New-Token, X-Token-Expired"),
                          Header.Raw(ci"X-Token-Expired", true.toString),
                          Header.Raw(ci"X-New-Token", token.value),
                        ),
                    )
                }.value
            },
          )
      }
    }
  }
}
