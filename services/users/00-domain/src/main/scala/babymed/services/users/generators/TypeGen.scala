package babymed.services.users.generators

import babymed.domain.Role
import babymed.refinements.{Password, Phone}
import babymed.services.users.domain.types.{FirstName, LastName, UserId}
import babymed.test.generators.Generators
import babymed.syntax.refined.commonSyntaxAutoRefineV
import org.scalacheck.Gen

import java.time.LocalDateTime

trait TypeGen extends Generators {
  val userIdGen: Gen[UserId] = idGen(UserId.apply)
  val firstNameGen: Gen[FirstName] = nonEmptyString.map(FirstName(_))
  val lastNameGen: Gen[LastName] = nonEmptyString.map(LastName(_))
  val roleGen: Gen[Role] = Gen.oneOf(Role.values)

  lazy val passwordGen: Gen[Password] = for {
    s1 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s2 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s3 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s4 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s5 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s6 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
  } yield s"$s1$s2$s3$s4$s5$s6"


}
