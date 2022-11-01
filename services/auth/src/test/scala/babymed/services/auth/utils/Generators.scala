package babymed.services.auth.utils

import org.scalacheck.Gen

import babymed.services.auth.domain.Credentials
import babymed.syntax.refined.commonSyntaxAutoRefineV
import babymed.test.generators.CommonGenerators

object Generators extends CommonGenerators {
  val credentialsGen: Gen[Credentials] = for {
    s0 <- phoneGen
    s1 <- nonEmptyStringGen(8)
  } yield Credentials(s0, s1)
}
