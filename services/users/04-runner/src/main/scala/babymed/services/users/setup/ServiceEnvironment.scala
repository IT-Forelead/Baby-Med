package babymed.services.users.setup

import cats.MonadThrow
import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import org.typelevel.log4cats.Logger
import skunk.Session

import babymed.services.users.ServerEnvironment
import babymed.services.users.boundary.Customers
import babymed.services.users.boundary.Users
import babymed.support.database.Migrations

case class ServiceEnvironment[F[_]: MonadThrow](
    config: Config,
    repositories: Repositories[F],
  ) {
  lazy val customers = new Customers[F](repositories.customers)
  lazy val users = new Users[F](repositories.users)
  lazy val toServer: ServerEnvironment[F] =
    ServerEnvironment(
      services = ServerEnvironment.Services(users, customers)
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
