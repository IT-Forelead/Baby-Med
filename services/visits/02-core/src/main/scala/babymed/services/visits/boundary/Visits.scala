package babymed.services.visits.boundary

import cats.Monad
import cats.implicits._

import babymed.domain.ResponseData
import babymed.services.visits.domain.CreateCheckupExpense
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.domain.PatientVisitReport
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.proto
import babymed.services.visits.repositories.CheckupExpensesRepository
import babymed.services.visits.repositories.OperationExpensesRepository
import babymed.services.visits.repositories.VisitsRepository

class Visits[F[_]: Monad](
    visitsRepository: VisitsRepository[F],
    checkupExpensesRepository: CheckupExpensesRepository[F],
    operationExpensesRepository: OperationExpensesRepository[F],
  ) extends proto.Visits[F] {
  override def create(createPatientVisit: CreatePatientVisit): F[PatientVisit] =
    visitsRepository.create(createPatientVisit)
  override def get(
      filters: PatientVisitFilters
    ): F[ResponseData[PatientVisitReport]] =
    for {
      visits <- visitsRepository.get(filters)
      total <- visitsRepository.getTotal(filters)
    } yield ResponseData(visits, total)
  override def updatePaymentStatus(id: PatientVisitId): F[PatientVisit] =
    for {
      update <- visitsRepository.updatePaymentStatus(id)
      visit <- visitsRepository.getVisitById(id)
      items <- visitsRepository.getItemsByVisitId(update.id)
      createCheckupExpense = items.map(item =>
        CreateCheckupExpense(serviceId = item.serviceId, visitId = item.visitId)
      )
      _ <- checkupExpensesRepository.create(createCheckupExpense)
      serviceIds = items.map(_.serviceId)
      _ <- operationExpensesRepository.createOperation(visit, serviceIds)
    } yield update
}
