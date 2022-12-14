package babymed.services.visits.boundary

import cats.Monad
import cats.implicits._

import babymed.domain.ResponseData
import babymed.services.visits.domain._
import babymed.services.visits.proto
import babymed.services.visits.repositories.CheckupExpensesRepository

class CheckupExpenses[F[_]: Monad](
    checkupExpensesRepository: CheckupExpensesRepository[F]
  ) extends proto.CheckupExpenses[F] {
  override def create(createCheckupExpense: CreateCheckupExpense): F[CheckupExpense] =
    checkupExpensesRepository.create(createCheckupExpense)
  override def createDoctorShare(createDoctorShare: CreateDoctorShare): F[DoctorShare] =
    checkupExpensesRepository.createDoctorShare(createDoctorShare)
  override def get(filters: CheckupExpenseFilters): F[ResponseData[CheckupExpenseInfo]] =
    for {
      checkupExpenses <- checkupExpensesRepository.get(filters)
      total <- checkupExpensesRepository.getTotal(filters)
    } yield ResponseData(checkupExpenses, total)
  override def getTotal(filters: CheckupExpenseFilters): F[Long] =
    checkupExpensesRepository.getTotal(filters)
  override def getDoctorShares: F[List[DoctorShareInfo]] =
    checkupExpensesRepository.getDoctorShares
}
