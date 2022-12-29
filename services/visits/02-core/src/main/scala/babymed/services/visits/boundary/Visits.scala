package babymed.services.visits.boundary

import cats.Monad
import cats.implicits._

import babymed.domain.ResponseData
import babymed.services.visits.domain.CreateCheckupExpense
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.domain.PatientVisitReport
import babymed.services.visits.domain.types.ChequeId
import babymed.services.visits.proto
import babymed.services.visits.repositories.CheckupExpensesRepository
import babymed.services.visits.repositories.VisitsRepository

class Visits[F[_]: Monad](
    visitsRepository: VisitsRepository[F],
    checkupExpensesRepository: CheckupExpensesRepository[F],
  ) extends proto.Visits[F] {
  override def create(createPatientVisit: CreatePatientVisit): F[Unit] =
    visitsRepository.create(createPatientVisit)
  override def get(filters: PatientVisitFilters): F[ResponseData[PatientVisitReport]] =
    visitsRepository.get(filters).map { visitList =>
      ResponseData(visitList, visitList.length.toLong)
    }
  override def updatePaymentStatus(chequeId: ChequeId): F[List[PatientVisit]] =
    for {
      update <- visitsRepository.updatePaymentStatus(chequeId)
      createCheckupExpense = update.map(visit =>
        CreateCheckupExpense(serviceId = visit.serviceId, visitId = visit.id)
      )
      _ <- checkupExpensesRepository.create(createCheckupExpense)
    } yield update
}
