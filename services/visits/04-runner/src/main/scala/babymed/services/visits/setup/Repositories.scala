package babymed.services.visits.setup

import cats.effect.Async
import cats.effect.Resource
import skunk.Session

import babymed.services.visits.repositories.VisitsRepository

case class Repositories[F[_]](
    visits: VisitsRepository[F]
  )
object Repositories {
  def make[F[_]: Async](
      implicit
      session: Resource[F, Session[F]]
    ): Repositories[F] =
    Repositories(
      visits = VisitsRepository.make[F]
    )
}
