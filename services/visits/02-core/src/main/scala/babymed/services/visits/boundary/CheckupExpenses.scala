package babymed.services.visits.boundary

import cats.Monad
import cats.implicits._

import babymed.domain.ResponseData
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.DoctorShareId
import babymed.services.visits.proto
import babymed.services.visits.repositories.CheckupExpensesRepository

class CheckupExpenses[F[_]: Monad](
    checkupExpensesRepository: CheckupExpensesRepository[F]
  ) extends proto.CheckupExpenses[F] {
  override def create(createCheckupExpenses: List[CreateCheckupExpense]): F[List[CheckupExpense]] =
    checkupExpensesRepository.create(createCheckupExpenses)
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
  override def deleteDoctorShare(id: DoctorShareId): F[Unit] =
    checkupExpensesRepository.deleteDoctorShare(id)
}
