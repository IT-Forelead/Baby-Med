package babymed.services.visits.setup

import cats.MonadThrow
import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import org.typelevel.log4cats.Logger
import skunk.Session

import babymed.services.visits.ServerEnvironment
import babymed.services.visits.boundary.Visits
import babymed.support.database.Migrations

case class ServiceEnvironment[F[_]: MonadThrow](
    config: Config,
    repositories: Repositories[F],
  ) {
  lazy val visits = new Visits[F](repositories.visits)
  lazy val toServer: ServerEnvironment[F] =
    ServerEnvironment(
      services = ServerEnvironment.Services(visits)
    )
}

object ServiceEnvironment {
  def make[F[_]: Async: Console: Logger]: Resource[F, ServiceEnvironment[F]] =
    for {
      config <- Resource.eval(ConfigLoader.load[F])
      _ <- Resource.eval(Migrations.run[F](config.migrations))

      resource <- ServiceResources.make[F](config)
      repositories = {
        implicit val session: Resource[F, Session[F]] = resource.postgres
        Repositories.make[F]
      }
    } yield ServiceEnvironment[F](config, repositories)
}
