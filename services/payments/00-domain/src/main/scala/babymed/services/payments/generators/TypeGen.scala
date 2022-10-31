package babymed.services.payments.generators

import babymed.services.payments.domain.types.{PaymentId, UZS}
import babymed.services.users.domain.types.CustomerId
import babymed.test.generators.Generators
import eu.timepit.refined.types.numeric.NonNegInt
import org.scalacheck.Gen
import squants.Money

trait TypeGen extends Generators{
  val paymentIdGen: Gen[PaymentId] = idGen(PaymentId.apply)
  val customerIdGen: Gen[CustomerId] = idGen(CustomerId.apply)
  val priceGen: Gen[Money] = Gen.posNum[Long].map(n => UZS(BigDecimal(n)))
  val nonNegIntGen: Gen[NonNegInt] = NonNegInt.unsafeFrom(Gen.choose(1, 2147483647).get)

}
