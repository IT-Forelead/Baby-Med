package babymed.services.users.setup

import cats.MonadThrow
import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import cats.implicits.toFunctorOps
import babymed.syntax.refined.commonSyntaxAutoRefineV
import babymed.services.users.boundary.Users
import babymed.services.users.repositories.UsersRepository
import org.typelevel.log4cats.Logger
import skunk.Session

case class ServiceEnvironment[F[_]: MonadThrow](
    config: Config,
    users: Users[F],
  )

object ServiceEnvironment {

  def make[F[_]: Async: Console: Logger]: Resource[F, ServiceEnvironment[F]] =
    for {
      config <- Resource.eval(ConfigLoader.load[F])
      resource <- ServiceResources.make[F](config)
      userRepository = {
        implicit val session: Resource[F, Session[F]] = resource.postgres
        UsersRepository.make[F]
      }
    } yield ServiceEnvironment[F](config, new Users[F](userRepository))
}
