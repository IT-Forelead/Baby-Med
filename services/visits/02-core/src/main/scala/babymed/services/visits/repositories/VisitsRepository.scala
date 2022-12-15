package babymed.services.visits.repositories

import cats.effect.Concurrent
import cats.effect.MonadCancel
import cats.effect.Resource
import cats.implicits._
import skunk.Session
import skunk.codec.all.int8
import skunk.implicits.toIdOps

import babymed.domain.ID
import babymed.effects.Calendar
import babymed.effects.GenUUID
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.domain.PatientVisitInfo
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.repositories.sql.VisitsSql
import babymed.support.skunk.syntax.all._

trait VisitsRepository[F[_]] {
  def create(createPatientVisit: CreatePatientVisit): F[PatientVisit]
  def get(filters: PatientVisitFilters): F[List[PatientVisitInfo]]
  def getTotal(filters: PatientVisitFilters): F[Long]
  def updatePaymentStatus(id: PatientVisitId): F[PatientVisit]
}

object VisitsRepository {
  def make[F[_]: GenUUID: Calendar: Concurrent](
      implicit
      session: Resource[F, Session[F]],
      F: MonadCancel[F, Throwable],
    ): VisitsRepository[F] = new VisitsRepository[F] {
    import sql.VisitsSql._

    override def create(createPatientVisit: CreatePatientVisit): F[PatientVisit] =
      for {
        id <- ID.make[F, PatientVisitId]
        now <- Calendar[F].currentDateTime
        visit <- insert.queryUnique(id ~ now ~ createPatientVisit)
      } yield visit

    override def get(filters: PatientVisitFilters): F[List[PatientVisitInfo]] = {
      val query = VisitsSql.select(filters).paginateOpt(filters.limit, filters.page)
      query.fragment.query(VisitsSql.decPaymentVisitInfo).queryList(query.argument)
    }

    override def getTotal(filters: PatientVisitFilters): F[Long] = {
      val query = VisitsSql.total(filters)
      query.fragment.query(int8).queryUnique(query.argument)
    }

    override def updatePaymentStatus(id: PatientVisitId): F[PatientVisit] =
      updatePaymentStatusSql.queryUnique(id)
  }
}
