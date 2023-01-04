package babymed.services.visits.repositories

import cats.effect.Concurrent
import cats.effect.Resource
import cats.implicits._
import skunk.Session
import skunk.codec.all.int8

import babymed.domain.ID
import babymed.domain.PaymentStatus.NotPaid
import babymed.effects.Calendar
import babymed.effects.GenUUID
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.repositories.sql.VisitsSql
import babymed.support.skunk.syntax.all._

trait VisitsRepository[F[_]] {
  def create(createPatientVisit: CreatePatientVisit): F[PatientVisit]
  def get(filters: PatientVisitFilters): F[List[PatientVisitReport]]
  def getTotal(filters: PatientVisitFilters): F[Long]
  def updatePaymentStatus(id: PatientVisitId): F[PatientVisit]
  def getItemsByVisitId(visitId: PatientVisitId): F[List[VisitItem]]
}

object VisitsRepository {
  def make[F[_]: GenUUID: Calendar: Concurrent](
      implicit
      session: Resource[F, Session[F]]
    ): VisitsRepository[F] = new VisitsRepository[F] {
    import sql.VisitsSql._

    override def create(createPatientVisit: CreatePatientVisit): F[PatientVisit] =
      for {
        id <- ID.make[F, PatientVisitId]
        now <- Calendar[F].currentDateTime
        visit <- insert.queryUnique(
          PatientVisit(
            id = id,
            createdAt = now,
            userId = createPatientVisit.userId,
            patientId = createPatientVisit.patientId,
            paymentStatus = NotPaid,
          )
        )
        list = createPatientVisit.serviceIds.map { serviceId =>
          PatientVisitItem(
            visitId = visit.id,
            serviceId = serviceId,
          )
        }
        _ <- insertItems(list).execute(list)
      } yield visit

    override def get(filters: PatientVisitFilters): F[List[PatientVisitReport]] = {
      val query = VisitsSql.select(filters).paginateOpt(filters.limit, filters.page)
      query.fragment.query(VisitsSql.decPaymentVisitInfo).queryList(query.argument).flatMap {
        _.traverse { el =>
          selectItemsSql.queryList(el.patientVisit.id).map { service =>
            PatientVisitReport(
              patientVisit = el.patientVisit,
              userFirstName = el.userFirstName,
              userLastName = el.userLastName,
              patient = el.patient,
              services = service,
              region = el.region,
              city = el.city,
            )
          }
        }
      }
    }

    override def getTotal(filters: PatientVisitFilters): F[Long] = {
      val query = VisitsSql.total(filters)
      query.fragment.query(int8).queryUnique(query.argument)
    }

    override def updatePaymentStatus(id: PatientVisitId): F[PatientVisit] =
      updatePaymentStatusSql.queryUnique(id)

    override def getItemsByVisitId(visitId: PatientVisitId): F[List[VisitItem]] =
      selectItemsByVisitIdSql.queryList(visitId)
  }
}
