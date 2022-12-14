package babymed.services.visits.generators

import org.scalacheck.Gen

import babymed.services.users.domain.types.UserId
import babymed.services.users.generators.UserGenerators
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.DoctorShareId
import babymed.services.visits.domain.types.ServiceId

trait CheckupExpenseGenerators extends TypeGen with UserGenerators with ServiceGenerators {
  lazy val checkupExpenseGen: Gen[CheckupExpense] =
    for {
      id <- checkupExpenseIdGen
      createdAt <- localDateTimeGen
      doctorShareId <- doctorShareIdGen
      price <- priceGen
    } yield CheckupExpense(id, createdAt, doctorShareId, price)

  def createCheckupExpenseGen(
      maybeDoctorShareId: Option[DoctorShareId] = None
    ): Gen[CreateCheckupExpense] =
    for {
      doctorShareId <- doctorShareIdGen
      price <- priceGen
    } yield CreateCheckupExpense(maybeDoctorShareId.getOrElse(doctorShareId), price)

  lazy val doctorShareGen: Gen[DoctorShare] =
    for {
      id <- doctorShareIdGen
      serviceId <- serviceIdGen
      userId <- userIdGen
      percent <- percentGen
    } yield DoctorShare(id, serviceId, userId, percent)

  def createDoctorShareGen(
      maybeServiceId: Option[ServiceId] = None,
      maybeUserId: Option[UserId] = None,
    ): Gen[CreateDoctorShare] =
    for {
      serviceId <- serviceIdGen
      userId <- userIdGen
      percent <- percentGen
    } yield CreateDoctorShare(
      maybeServiceId.getOrElse(serviceId),
      maybeUserId.getOrElse(userId),
      percent,
    )

  lazy val checkupExpenseInfoGen: Gen[CheckupExpenseInfo] =
    for {
      checkupExpense <- checkupExpenseGen
      doctorShare <- doctorShareGen
      service <- serviceWithTypeNameGen
      user <- userGen
    } yield CheckupExpenseInfo(checkupExpense, doctorShare, service, user)

  lazy val doctorShareInfoGen: Gen[DoctorShareInfo] =
    for {
      doctorShare <- doctorShareGen
      service <- serviceWithTypeNameGen
      user <- userGen
    } yield DoctorShareInfo(doctorShare, service, user)
}
