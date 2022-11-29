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
  val paymentStatusGen: Gen[PaymentStatus] = Gen.oneOf(PaymentStatus.values)
  val serviceNameGen: Gen[ServiceName] = nonEmptyString.map(ServiceName(_))
  val serviceTypeNameGen: Gen[ServiceTypeName] = nonEmptyString.map(ServiceTypeName(_))
  val priceGen: Gen[Money] = Gen.posNum[Long].map(n => UZS(BigDecimal(n)))
}
