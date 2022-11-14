package babymed.services.visits.repositories

import cats.effect.Concurrent
import cats.effect.MonadCancel
import cats.effect.Resource
import skunk.Session

import babymed.effects.Calendar
import babymed.effects.GenUUID

trait VisitsRepository[F[_]] {}

object VisitsRepository {
  def make[F[_]: GenUUID: Calendar: Concurrent](
      implicit
      session: Resource[F, Session[F]],
      F: MonadCancel[F, Throwable],
    ): VisitsRepository[F] = new VisitsRepository[F] {}
}
