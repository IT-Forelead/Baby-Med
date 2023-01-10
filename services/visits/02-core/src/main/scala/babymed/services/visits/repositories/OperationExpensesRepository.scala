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
import babymed.services.visits.domain.types.OperationId
import babymed.services.visits.domain.types.OperationServiceId
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.repositories.sql.OperationExpensesSql
import babymed.support.skunk.syntax.all._

trait OperationExpensesRepository[F[_]] {
  def create(createOperationExpense: CreateOperationExpense): F[OperationExpense]
  def createOperation(visit: PatientVisit, serviceIds: List[ServiceId]): F[Unit]
  def get(filters: OperationExpenseFilters): F[List[OperationExpenseInfo]]
  def getTotal(filters: OperationExpenseFilters): F[Long]
  def getOperations(filters: OperationFilters): F[List[OperationInfo]]
  def getOperationsTotal(filters: OperationFilters): F[Long]
  def getItemsById(id: OperationExpenseId): F[List[OperationExpenseItemWithUser]]
  def createOperationServices(serviceId: ServiceId): F[OperationService]
  def getOperationServices: F[List[OperationServiceInfo]]
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
            operationId = createOperationExpense.operationId,
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

    override def createOperation(visit: PatientVisit, serviceIds: List[ServiceId]): F[Unit] =
      for {
        operationServices <- selectOperationServiceIds.all
        now <- Calendar[F].currentDateTime
        operations <- serviceIds
          .filter { serviceId =>
            operationServices.contains(serviceId)
          }
          .traverse { serviceId =>
            ID.make[F, OperationId].map { id =>
              Operation(
                id = id,
                createdAt = now,
                patientId = visit.patientId,
                serviceId = serviceId,
              )
            }
          }
        _ <- insertOperations(operations).execute(operations)
      } yield {}

    override def get(
        filters: OperationExpenseFilters
      ): F[List[OperationExpenseInfo]] = {
      val query = OperationExpensesSql.select(filters).paginateOpt(filters.limit, filters.page)
      query.fragment.query(OperationExpensesSql.decOperationExpenseInfo).queryList(query.argument)
    }

    override def getTotal(filters: OperationExpenseFilters): F[Long] = {
      val query = OperationExpensesSql.total(filters)
      query.fragment.query(int8).queryUnique(query.argument)
    }

    override def getOperations(filters: OperationFilters): F[List[OperationInfo]] = {
      val query =
        OperationExpensesSql.selectOperations(filters).paginateOpt(filters.limit, filters.page)
      query.fragment.query(OperationExpensesSql.decOperationInfo).queryList(query.argument)
    }

    override def getOperationsTotal(filters: OperationFilters): F[Long] = {
      val query = OperationExpensesSql.operationTotal(filters)
      query.fragment.query(int8).queryUnique(query.argument)
    }

    override def getItemsById(id: OperationExpenseId): F[List[OperationExpenseItemWithUser]] =
      selectItemsSql.queryList(id)

    override def createOperationServices(serviceId: ServiceId): F[OperationService] =
      ID.make[F, OperationServiceId].flatMap { id =>
        insertOperationService.queryUnique(OperationService(id, serviceId))
      }

    override def getOperationServices: F[List[OperationServiceInfo]] =
      selectOperationServices.all
  }
}
