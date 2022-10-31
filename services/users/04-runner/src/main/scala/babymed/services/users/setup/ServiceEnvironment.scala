package babymed.services.users.setup

import babymed.services.users.boundary.Customers
import babymed.services.users.boundary.Users
import babymed.services.users.repositories.CustomersRepository
import babymed.services.users.repositories.UsersRepository
import cats.MonadThrow
import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import org.typelevel.log4cats.Logger
import skunk.Session

case class ServiceEnvironment[F[_]: MonadThrow](
    config: Config,
    users: Users[F],
    customers: Customers[F],
  )

object ServiceEnvironment {
  def make[F[_]: Async: Console: Logger]: Resource[F, ServiceEnvironment[F]] =
    for {
      config <- Resource.eval(ConfigLoader.load[F])
      resource <- ServiceResources.make[F](config)
      usersRepository = {
        implicit val session: Resource[F, Session[F]] = resource.postgres
        UsersRepository.make[F]
      }
      customersRepository = {
        implicit val session: Resource[F, Session[F]] = resource.postgres
        CustomersRepository.make[F]
      }
    } yield ServiceEnvironment[F](
      config,
      new Users[F](usersRepository),
      new Customers[F](customersRepository),
    )
}
