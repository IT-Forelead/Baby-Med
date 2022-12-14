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
      new PatientRouters[F](env.security, env.services.patients).routes,
      new ServiceRouters[F](env.security, env.services.services).routes,
      new VisitRouters[F](env.security, env.services.visits).routes,
      new OperationExpenseRouters[F](env.security, env.services.operationExpenses).routes,
      new CheckupExpenseRouters[F](env.security, env.services.checkupExpenses).routes,
    )

  def make[F[_]: Async](
      env: ServerEnvironment[F]
    )(implicit
      logger: Logger[F]
    ): Resource[F, F[ExitCode]] =
    HttpServer.make[F](env.config.http, allRoutes[F](env)).map { _ =>
      logger.info(s"HTTP Server is started").as(ExitCode.Success)
    }
}
