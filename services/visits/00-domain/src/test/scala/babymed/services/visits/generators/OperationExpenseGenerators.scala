package babymed.services.visits.generators

import org.scalacheck.Gen

import babymed.services.users.generators.UserGenerators
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.PatientVisitId

trait OperationExpenseGenerators
    extends TypeGen
       with UserGenerators
       with PatientVisitGenerators
       with ServiceGenerators {
  lazy val operationExpenseGen: Gen[OperationExpense] =
    for {
      id <- operationExpenseIdGen
      createdAt <- localDateTimeGen
      patientVisitId <- patientVisitIdGen
      forLaboratory <- priceGen
      forTools <- priceGen
      forDrugs <- priceGen
      partnerDoctorFullName <- partnerDoctorFullNameGen.opt
      partnerDoctorPrice <- priceGen.opt
    } yield OperationExpense(
      id,
      createdAt,
      patientVisitId,
      forLaboratory,
      forTools,
      forDrugs,
      partnerDoctorFullName,
      partnerDoctorPrice,
    )

  lazy val operationExpenseItemGen: Gen[OperationExpenseItem] =
    for {
      operationExpenseId <- operationExpenseIdGen
      userId <- userIdGen
      subRoleId <- subRoleIdGen
      price <- priceGen
    } yield OperationExpenseItem(
      operationExpenseId,
      userId,
      subRoleId,
      price,
    )

  lazy val createOperationExpenseItemGen: Gen[CreateOperationExpenseItem] =
    for {
      userId <- userIdGen
      subRoleId <- subRoleIdGen
      price <- priceGen
    } yield CreateOperationExpenseItem(userId, subRoleId, price)

  def createOperationExpenseGen(
      maybePatientVisitId: Option[PatientVisitId] = None
    ): Gen[CreateOperationExpense] =
    for {
      patientVisitId <- patientVisitIdGen
      items <- createOperationExpenseItemGen
      forLaboratory <- priceGen
      forTools <- priceGen
      forDrugs <- priceGen
      partnerDoctorFullName <- partnerDoctorFullNameGen.opt
      partnerDoctorPrice <- priceGen.opt
    } yield CreateOperationExpense(
      maybePatientVisitId.getOrElse(patientVisitId),
      List(items),
      forLaboratory,
      forTools,
      forDrugs,
      partnerDoctorFullName,
      partnerDoctorPrice,
    )

  lazy val operationExpenseItemWithUserGen: Gen[OperationExpenseItemWithUser] =
    for {
      items <- operationExpenseItemGen
      user <- userGen
      subRole <- subRoleGen
    } yield OperationExpenseItemWithUser(items, user, subRole)

  lazy val operationExpenseWithPatientVisitGen: Gen[OperationExpenseWithPatientVisit] =
    for {
      operationExpense <- operationExpenseGen
      user <- patientVisitGen
      patient <- patientGen
      service <- serviceGen
    } yield OperationExpenseWithPatientVisit(operationExpense, user, patient, service)
}
