package babymed.services.payments.generators

import eu.timepit.refined.scalacheck.all.greaterEqualArbitrary
import eu.timepit.refined.types.numeric.NonNegInt
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import squants.Money

import babymed.services.payments.domain.types.PaymentId
import babymed.services.payments.domain.types.UZS
import babymed.test.generators.Generators

trait TypeGen extends Generators {
  val paymentIdGen: Gen[PaymentId] = idGen(PaymentId.apply)
  val priceGen: Gen[Money] = Gen.posNum[Long].map(n => UZS(BigDecimal(n)))
  val nonNegIntGen: Gen[NonNegInt] = Arbitrary.arbitrary[NonNegInt]
}
