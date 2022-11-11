package babymed.services.visits.generators

import org.scalacheck.Gen
import squants.Money

import babymed.test.generators.Generators

trait TypeGen extends Generators {
  val priceGen: Gen[Money] = Gen.posNum[Long].map(n => UZS(BigDecimal(n)))
}
