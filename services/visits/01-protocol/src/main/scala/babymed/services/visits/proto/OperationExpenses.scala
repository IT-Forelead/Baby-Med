package babymed.services.visits.proto

import babymed.domain.ResponseData
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.OperationExpenseId
import babymed.support.services.service

@service(Custom)
trait OperationExpenses[F[_]] {
  def create(createOperationExpense: CreateOperationExpense): F[OperationExpense]
  def get(filters: OperationExpenseFilters): F[ResponseData[OperationExpenseWithPatientVisit]]
  def getTotal(filters: OperationExpenseFilters): F[Long]
  def getItemsById(id: OperationExpenseId): F[List[OperationExpenseItemWithUser]]
}

object OperationExpenses {}
