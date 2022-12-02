package babymed.services.visits.repositories

import cats.effect._
import cats.implicits._
import skunk.Session
import skunk.codec.all.int8

import babymed.domain.ID
import babymed.effects.Calendar
import babymed.effects.GenUUID
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.OperationExpenseId
import babymed.services.visits.repositories.sql.OperationExpensesSql
import babymed.support.skunk.syntax.all._

trait OperationExpensesRepository[F[_]] {
  def create(createOperationExpense: CreateOperationExpense): F[OperationExpense]
  def get(filters: OperationExpenseFilters): F[List[OperationExpenseWithPatientVisit]]
  def getTotal(filters: OperationExpenseFilters): F[Long]
  def getItemsById(id: OperationExpenseId): F[List[OperationExpenseItemWithUser]]
}

object OperationExpensesRepository {
  def make[F[_]: GenUUID: Calendar: Concurrent](
      implicit
      session: Resource[F, Session[F]],
      F: MonadCancel[F, Throwable],
    ): OperationExpensesRepository[F] = new OperationExpensesRepository[F] {
    import sql.OperationExpensesSql._

    override def create(createOperationExpense: CreateOperationExpense): F[OperationExpense] =
      for {
        id <- ID.make[F, OperationExpenseId]
        now <- Calendar[F].currentDateTime
        operationExpense <- insert.queryUnique(
          OperationExpense(
            id = id,
            createdAt = now,
            patientVisitId = createOperationExpense.patientVisitId,
            forLaboratory = createOperationExpense.forLaboratory,
            forTools = createOperationExpense.forTools,
            forDrugs = createOperationExpense.forDrugs,
            partnerDoctorFullName = createOperationExpense.partnerDoctorFullName,
            partnerDoctorPrice = createOperationExpense.partnerDoctorPrice,
          )
        )
        list = createOperationExpense.operationExpenseItems.map { item =>
          OperationExpenseItem(
            operationExpenseId = operationExpense.id,
            userId = item.userId,
            subRoleId = item.subRoleId,
            price = item.price,
          )
        }
        _ <- insertItems(list).execute(list)
      } yield operationExpense

    override def get(
        filters: OperationExpenseFilters
      ): F[List[OperationExpenseWithPatientVisit]] = {
      val query = OperationExpensesSql.select(filters).paginateOpt(filters.limit, filters.page)
      query.fragment.query(OperationExpensesSql.decOEWithPatientVisit).queryList(query.argument)
    }

    override def getTotal(filters: OperationExpenseFilters): F[Long] = {
      val query = OperationExpensesSql.total(filters)
      query.fragment.query(int8).queryUnique(query.argument)
    }

    override def getItemsById(id: OperationExpenseId): F[List[OperationExpenseItemWithUser]] =
      selectItemsSql.queryList(id)
  }
}
