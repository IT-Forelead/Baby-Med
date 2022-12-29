package babymed.services.visits.repositories

import cats.effect.Concurrent
import cats.effect.Resource
import cats.implicits._
import skunk.Session

import babymed.domain.ID
import babymed.effects.Calendar
import babymed.effects.GenUUID
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.InsertPatientVisit
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.domain.PatientVisitReport
import babymed.services.visits.domain.types.ChequeId
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.repositories.sql.VisitsSql
import babymed.support.skunk.syntax.all._

trait VisitsRepository[F[_]] {
  def create(createPatientVisits: CreatePatientVisit): F[Unit]
  def get(filters: PatientVisitFilters): F[List[PatientVisitReport]]
  def updatePaymentStatus(chequeId: ChequeId): F[List[PatientVisit]]
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
        visits <- createPatientVisits
          .serviceIds
          .traverse(serviceId =>
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

    override def get(filters: PatientVisitFilters): F[List[PatientVisitReport]] = {
      val query = VisitsSql.select(filters).paginateOpt(filters.limit, filters.page)
      query.fragment.query(VisitsSql.decPaymentVisitInfo).queryList(query.argument).map { list =>
        list
          .groupBy(_.patientVisit.chequeId)
          .map { el =>
            PatientVisitReport(
              patientVisits = el._2.map(_.patientVisit),
              userFirstName = el._2.map(_.userFirstName).distinct.head,
              userLastName = el._2.map(_.userLastName).distinct.head,
              patient = el._2.map(_.patient).distinct.head,
              services = el._2.map(_.service),
              region = el._2.map(_.region).distinct.head,
              city = el._2.map(_.city).distinct.head,
            )
          }
          .toList
      }
    }

    override def updatePaymentStatus(chequeId: ChequeId): F[List[PatientVisit]] =
      updatePaymentStatusSql.queryList(chequeId)
  }
}
