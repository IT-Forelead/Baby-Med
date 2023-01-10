package babymed.services.visits.generators

import org.scalacheck.Gen

import babymed.services.users.generators.UserGenerators
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.OperationId

trait OperationExpenseGenerators
    extends TypeGen
       with UserGenerators
       with PatientVisitGenerators
       with ServiceGenerators {
  lazy val operationExpenseGen: Gen[OperationExpense] =
    for {
      id <- operationExpenseIdGen
      createdAt <- localDateTimeGen
      operationId <- operationIdGen
      forLaboratory <- priceGen
      forTools <- priceGen
      forDrugs <- priceGen
      partnerDoctorFullName <- partnerDoctorFullNameGen.opt
      partnerDoctorPrice <- priceGen.opt
    } yield OperationExpense(
      id,
      createdAt,
      operationId,
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
      maybeOperationId: Option[OperationId] = None
    ): Gen[CreateOperationExpense] =
    for {
      operationId <- operationIdGen
      items <- createOperationExpenseItemGen
      forLaboratory <- priceGen
      forTools <- priceGen
      forDrugs <- priceGen
      partnerDoctorFullName <- partnerDoctorFullNameGen.opt
      partnerDoctorPrice <- priceGen.opt
    } yield CreateOperationExpense(
      maybeOperationId.getOrElse(operationId),
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

  lazy val operationGen: Gen[Operation] =
    for {
      id <- operationIdGen
      createdAt <- localDateTimeGen
      patientId <- patientIdGen
      serviceId <- serviceIdGen
    } yield Operation(id, createdAt, patientId, serviceId)

  lazy val operationServiceGen: Gen[OperationService] =
    for {
      id <- operationServiceIdGen
      serviceId <- serviceIdGen
    } yield OperationService(id, serviceId)

  lazy val operationServiceInfoGen: Gen[OperationServiceInfo] =
    for {
      operationService <- operationServiceGen
      service <- serviceWithTypeNameGen
    } yield OperationServiceInfo(operationService, service)

  lazy val operationInfoGen: Gen[OperationInfo] =
    for {
      operation <- operationGen
      service <- serviceWithTypeNameGen
      patient <- patientGen
      region <- regionGen
      city <- cityGen
    } yield OperationInfo(operation, service, patient, region, city)

  lazy val operationExpenseWithPatientVisitGen: Gen[OperationExpenseInfo] =
    for {
      operationExpense <- operationExpenseGen
      operation <- operationGen
      patient <- patientGen
      service <- serviceWithTypeNameGen
    } yield OperationExpenseInfo(operationExpense, operation, patient, service)
}
