package babymed.services.visits.generators

import org.scalacheck.Gen
import squants.Money

import babymed.services.visits.domain.types._
import babymed.syntax.refined.commonSyntaxAutoRefineV
import babymed.test.generators.Generators

trait TypeGen extends Generators {
  val serviceIdGen: Gen[ServiceId] = idGen(ServiceId.apply)
  val serviceNameGen: Gen[ServiceName] = nonEmptyString.map(ServiceName(_))
  val costGen: Gen[Money] = Gen.posNum[Long].map(n => UZS(BigDecimal(n)))
}
