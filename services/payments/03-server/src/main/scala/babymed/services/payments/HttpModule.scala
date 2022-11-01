package babymed.services.payments

import cats.Monad
import cats.MonadThrow
import cats.data.NonEmptyList
import cats.effect.Async
import cats.effect.ExitCode
import cats.effect.kernel.Resource
import cats.implicits.toFunctorOps
import org.http4s.HttpRoutes
import org.http4s.circe.JsonDecoder
import org.typelevel.log4cats.Logger

import babymed.support.services.http4s.HealthHttpRoutes
import babymed.support.services.http4s.HttpServer
import babymed.support.services.http4s.HttpServerConfig

object HttpModule {
  private def allRoutes[F[_]: Monad: MonadThrow: JsonDecoder: Logger]: NonEmptyList[HttpRoutes[F]] =
    NonEmptyList.of[HttpRoutes[F]](
      new HealthHttpRoutes[F].routes
    )

  def make[F[_]: Async](
      config: HttpServerConfig
    )(implicit
      logger: Logger[F]
    ): Resource[F, F[ExitCode]] =
    HttpServer.make[F](config, allRoutes[F]).map { _ =>
      logger.info(s"Users service http server is started").as(ExitCode.Success)
    }
}
