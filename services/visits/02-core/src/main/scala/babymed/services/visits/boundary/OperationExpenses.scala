package babymed.services.visits.boundary

import cats.Monad
import cats.implicits._

import babymed.domain.ResponseData
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.OperationExpenseId
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.proto
import babymed.services.visits.repositories.OperationExpensesRepository

class OperationExpenses[F[_]: Monad](operationExpensesRepository: OperationExpensesRepository[F])
    extends proto.OperationExpenses[F] {
  override def create(createOperationExpense: CreateOperationExpense): F[OperationExpense] =
    operationExpensesRepository.create(createOperationExpense)
  override def get(
      filters: OperationExpenseFilters
    ): F[ResponseData[OperationExpenseWithPatientVisit]] =
    for {
      operationExpenses <- operationExpensesRepository.get(filters)
      total <- operationExpensesRepository.getTotal(filters)
    } yield ResponseData(operationExpenses, total)
  override def getTotal(filters: OperationExpenseFilters): F[Long] =
    operationExpensesRepository.getTotal(filters)
  override def getItemsById(id: OperationExpenseId): F[List[OperationExpenseItemWithUser]] =
    operationExpensesRepository.getItemsById(id)
  override def createOperationServices(serviceId: ServiceId): F[OperationService] =
    operationExpensesRepository.createOperationServices(serviceId)
  override def getOperationServices: F[List[OperationServiceInfo]] =
    operationExpensesRepository.getOperationServices
}
