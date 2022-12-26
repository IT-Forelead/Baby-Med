package babymed.services.visits.repositories

import cats.effect.Concurrent
import cats.effect.Resource
import cats.implicits._
import skunk.Session
import skunk.codec.all.int8

import babymed.domain.ID
import babymed.effects.Calendar
import babymed.effects.GenUUID
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.InsertPatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.domain.PatientVisitInfo
import babymed.services.visits.domain.types.ChequeId
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.repositories.sql.VisitsSql
import babymed.support.skunk.syntax.all._

trait VisitsRepository[F[_]] {
  def create(createPatientVisits: CreatePatientVisit): F[Unit]
  def get(filters: PatientVisitFilters): F[List[PatientVisitInfo]]
  def getTotal(filters: PatientVisitFilters): F[Long]
  def updatePaymentStatus(chequeId: ChequeId): F[Unit]
}

object VisitsRepository {
  def make[F[_]: GenUUID: Calendar: Concurrent](
      implicit
      session: Resource[F, Session[F]]
    ): VisitsRepository[F] = new VisitsRepository[F] {
    import sql.VisitsSql._

    override def create(createPatientVisits: CreatePatientVisit): F[Unit] =
      for {
        chequeId <- ID.make[F, ChequeId]
        now <- Calendar[F].currentDateTime
        visits <- createPatientVisits.serviceIds.traverse(serviceId =>
          ID.make[F, PatientVisitId]
            .map(pVId =>
              InsertPatientVisit(
                pVId,
                now,
                createPatientVisits.userId,
                createPatientVisits.patientId,
                serviceId,
                chequeId,
              )
            )
        )
        _ <- VisitsSql.insertItems(visits).execute(visits)
      } yield {}

    override def get(filters: PatientVisitFilters): F[List[PatientVisitInfo]] = {
      val query = VisitsSql.select(filters).paginateOpt(filters.limit, filters.page)
      query.fragment.query(VisitsSql.decPaymentVisitInfo).queryList(query.argument)
    }

    override def getTotal(filters: PatientVisitFilters): F[Long] = {
      val query = VisitsSql.total(filters)
      query.fragment.query(int8).queryUnique(query.argument)
    }

    override def updatePaymentStatus(chequeId: ChequeId): F[Unit] =
      updatePaymentStatusSql.execute(chequeId)
  }
}
