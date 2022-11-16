package babymed.services.visits.setup

import cats.effect.Async
import cats.effect.Resource
import skunk.Session

import babymed.services.visits.repositories.ServicesRepository
import babymed.services.visits.repositories.VisitsRepository

case class Repositories[F[_]](
    services: ServicesRepository[F],
    visits: VisitsRepository[F],
  )
object Repositories {
  def make[F[_]: Async](
      implicit
      session: Resource[F, Session[F]]
    ): Repositories[F] =
    Repositories(
      services = ServicesRepository.make[F],
      visits = VisitsRepository.make[F],
    )
}
