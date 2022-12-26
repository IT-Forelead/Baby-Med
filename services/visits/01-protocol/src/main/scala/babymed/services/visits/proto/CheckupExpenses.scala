package babymed.services.visits.proto

import babymed.domain.ResponseData
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.DoctorShareId
import babymed.services.visits.domain.types.ServiceId
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec

@service(Custom)
trait CheckupExpenses[F[_]] {
  def create(serviceId: ServiceId): F[CheckupExpense]
  def createDoctorShare(createData: CreateDoctorShare): F[DoctorShare]
  def get(filters: CheckupExpenseFilters): F[ResponseData[CheckupExpenseInfo]]
  def getTotal(filters: CheckupExpenseFilters): F[Long]
  def getDoctorShares: F[List[DoctorShareInfo]]
  def deleteDoctorShare(id: DoctorShareId): F[Unit]
}

object CheckupExpenses {}
