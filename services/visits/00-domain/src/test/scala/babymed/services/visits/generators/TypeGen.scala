package babymed.services.visits.generators

import org.scalacheck.Gen
import squants.Money

import babymed.domain.PaymentStatus
import babymed.services.visits.domain.types._
import babymed.syntax.refined.commonSyntaxAutoRefineV
import babymed.test.generators.Generators

trait TypeGen extends Generators {
  val serviceIdGen: Gen[ServiceId] = idGen(ServiceId.apply)
  val serviceTypeIdGen: Gen[ServiceTypeId] = idGen(ServiceTypeId.apply)
  val patientVisitIdGen: Gen[PatientVisitId] = idGen(PatientVisitId.apply)
  val operationExpenseIdGen: Gen[OperationExpenseId] = idGen(OperationExpenseId.apply)
  val checkupExpenseIdGen: Gen[CheckupExpenseId] = idGen(CheckupExpenseId.apply)
  val doctorShareIdGen: Gen[DoctorShareId] = idGen(DoctorShareId.apply)
  val chequeIdGen: Gen[ChequeId] = idGen(ChequeId.apply)
  val paymentStatusGen: Gen[PaymentStatus] = Gen.oneOf(PaymentStatus.values)
  val serviceNameGen: Gen[ServiceName] = nonEmptyString.map(ServiceName(_))
  val serviceTypeNameGen: Gen[ServiceTypeName] = nonEmptyString.map(ServiceTypeName(_))
  val partnerDoctorFullNameGen: Gen[PartnerDoctorFullName] =
    nonEmptyString.map(PartnerDoctorFullName(_))
  val priceGen: Gen[Money] = Gen.posNum[Long].map(n => UZS(BigDecimal(n)))
}
