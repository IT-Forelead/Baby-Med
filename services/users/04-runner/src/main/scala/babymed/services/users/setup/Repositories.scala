package babymed.services.users.setup

import cats.effect.Async
import cats.effect.Resource
import skunk.Session

import babymed.services.users.repositories.PatientsRepository
import babymed.services.users.repositories.UsersRepository

case class Repositories[F[_]](
    users: UsersRepository[F],
    patients: PatientsRepository[F],
  )
object Repositories {
  def make[F[_]: Async](
      implicit
      session: Resource[F, Session[F]]
    ): Repositories[F] =
    Repositories(
      users = UsersRepository.make[F],
      patients = PatientsRepository.make[F],
    )
}
