package babymed.services.visits.repositories

import cats.effect._
import cats.implicits.toFlatMapOps
import skunk._
import skunk.implicits.toIdOps
import babymed.domain.ID
import babymed.effects.Calendar
import babymed.effects.GenUUID
import babymed.services.visits.domain.{CreateService, EditService, Service, ServiceType}
import babymed.services.visits.domain.types.{ServiceId, ServiceTypeId, ServiceTypeName}
import babymed.support.skunk.syntax.all._

trait ServicesRepository[F[_]] {
  def create(createService: CreateService): F[Service]
  def get(serviceTypeId: ServiceTypeId): F[List[Service]]
  def edit(editService: EditService): F[Unit]
  def delete(serviceId: ServiceId): F[Unit]
  def createServiceType(name: ServiceTypeName): F[ServiceType]
  def getServiceTypes: F[List[ServiceType]]
  def deleteServiceType(id: ServiceTypeId): F[Unit]
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

    override def get(serviceTypeId: ServiceTypeId): F[List[Service]] =
      selectSql.queryList(serviceTypeId)
    override def edit(editService: EditService): F[Unit] =
      updateSql.execute(editService)
    override def delete(serviceId: ServiceId): F[Unit] =
      deleteSql.execute(serviceId)

    override def createServiceType(name: ServiceTypeName): F[ServiceType] =
      ID.make[F, ServiceTypeId].flatMap { id =>
        insertServiceTypeSql.queryUnique(id ~ name)
      }

    override def getServiceTypes: F[List[ServiceType]] =
      selectServiceTypesSql.all
    override def deleteServiceType(serviceId: ServiceTypeId): F[Unit] =
      deleteServiceTypeSql.execute(serviceId)
  }
}
