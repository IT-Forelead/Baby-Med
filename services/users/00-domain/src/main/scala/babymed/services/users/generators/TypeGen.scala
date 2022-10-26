package babymed.services.users.generators

import babymed.{Password, Phone}
import babymed.domain.Role
import babymed.services.users.domain.types.{FirstName, LastName, UserId}
import babymed.test.generators.Generators
import babymed.syntax.refined.commonSyntaxAutoRefineV
import org.scalacheck.Gen

trait TypeGen extends Generators {
  val userIdGen: Gen[UserId] = idGen(UserId.apply)
  val firstNameGen: Gen[FirstName] = nonEmptyString.map(FirstName(_))
  val lastNameGen: Gen[LastName] = nonEmptyString.map(LastName(_))
  val phoneGen: Gen[Phone] = numberGen(12).map("+" + _)
  val roleGen: Gen[Role] = Gen.oneOf(Role.values)

  val passwordGen: Gen[Password] = for {
    s0 <- Gen.alphaUpperChar
    s1 <- nonEmptyStringGen(5, 8)
    s2 <- numberGen(1)
    s3 <- Gen.oneOf("!@#$%^&*")
  } yield s"$s0$s1$s2$s3"
}
