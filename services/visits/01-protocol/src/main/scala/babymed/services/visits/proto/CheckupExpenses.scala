package babymed.services.visits.proto

import babymed.domain.ResponseData
import babymed.services.visits.domain._
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec

@service(Custom)
trait CheckupExpenses[F[_]] {
  def create(createCheckupExpense: CreateCheckupExpense): F[CheckupExpense]
  def createDoctorShare(createData: CreateDoctorShare): F[DoctorShare]
  def get(filters: CheckupExpenseFilters): F[ResponseData[CheckupExpenseInfo]]
  def getTotal(filters: CheckupExpenseFilters): F[Long]
  def getDoctorShares: F[List[DoctorShareInfo]]
}

object CheckupExpenses {}
