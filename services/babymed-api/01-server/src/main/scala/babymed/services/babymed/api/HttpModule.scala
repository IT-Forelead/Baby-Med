package babymed.services.babymed.api

import cats.data.NonEmptyList
import cats.effect.Async
import cats.effect.ExitCode
import cats.effect.kernel.Resource
import cats.implicits.toFunctorOps
import org.http4s.HttpRoutes
import org.http4s.circe.JsonDecoder
import org.typelevel.log4cats.Logger

import babymed.services.babymed.api.routes._
import babymed.support.services.http4s.HttpServer

object HttpModule {
  private def allRoutes[F[_]: Async: JsonDecoder: Logger](
      env: ServerEnvironment[F]
    ): NonEmptyList[HttpRoutes[F]] =
    NonEmptyList.of[HttpRoutes[F]](
      new AuthRoutes[F](env.security).routes,
      new UserRouters[F](env.security, env.services.users).routes,
      new CustomerRouters[F](env.security, env.services.customers).routes,
      new PaymentRouters[F](env.security, env.services.payments).routes,
    )

  def make[F[_]: Async](
      env: ServerEnvironment[F]
    )(implicit
      logger: Logger[F]
    ): Resource[F, F[ExitCode]] =
    HttpServer.make[F](env.config.http, allRoutes[F](env)).map { _ =>
      logger.info(s"Mexico service http server is started").as(ExitCode.Success)
    }
}
