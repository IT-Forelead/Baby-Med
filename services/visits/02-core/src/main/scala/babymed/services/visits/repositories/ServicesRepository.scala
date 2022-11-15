package babymed.services.visits.repositories

import cats.effect._
import cats.implicits.toFlatMapOps
import skunk._
import skunk.implicits.toIdOps

import babymed.domain.ID
import babymed.effects.Calendar
import babymed.effects.GenUUID
import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.EditService
import babymed.services.visits.domain.Service
import babymed.services.visits.domain.types.ServiceId
import babymed.support.skunk.syntax.all._

trait ServicesRepository[F[_]] {
  def create(createService: CreateService): F[Service]
  def get: F[List[Service]]
  def edit(editService: EditService): F[Unit]
  def delete(serviceId: ServiceId): F[Unit]
}

object ServicesRepository {
  def make[F[_]: GenUUID: Calendar: Concurrent](
      implicit
      session: Resource[F, Session[F]],
      F: MonadCancel[F, Throwable],
    ): ServicesRepository[F] = new ServicesRepository[F] {
    import sql.ServicesSql._

    override def create(createService: CreateService): F[Service] =
      ID.make[F, ServiceId].flatMap { id =>
        insertSql.queryUnique(id ~ createService)
      }

    override def get: F[List[Service]] =
      selectSql.queryList(Void)
    override def edit(editService: EditService): F[Unit] =
      updateSql.execute(editService)
    override def delete(serviceId: ServiceId): F[Unit] =
      deleteSql.execute(serviceId)
  }
}
