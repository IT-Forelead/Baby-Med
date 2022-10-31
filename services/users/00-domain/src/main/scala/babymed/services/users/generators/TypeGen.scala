package babymed.services.users.generators

import java.time.LocalDateTime

import babymed.Password
import babymed.Phone
import babymed.domain.Role
import babymed.services.users.domain.types.FirstName
import babymed.services.users.domain.types.LastName
import babymed.services.users.domain.types.UserId
import babymed.syntax.refined.commonSyntaxAutoRefineV
import babymed.test.generators.Generators
import org.scalacheck.Gen

trait TypeGen extends Generators {
  val userIdGen: Gen[UserId] = idGen(UserId.apply)
  val firstNameGen: Gen[FirstName] = nonEmptyString.map(FirstName(_))
  val lastNameGen: Gen[LastName] = nonEmptyString.map(LastName(_))
  val phoneGen: Gen[Phone] = numberGen(12).map("+" + _)
  val roleGen: Gen[Role] = Gen.oneOf(Role.values)

  val passwordGen: Gen[Password] = for {
    s1 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s2 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s3 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s4 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s5 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s6 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
  } yield s"$s1$s2$s3$s4$s5$s6"

  val timestampGen: Gen[LocalDateTime] = for {
    year <- Gen.choose(1800, 2100)
    month <- Gen.choose(1, 12)
    day <- Gen.choose(1, 28)
    hour <- Gen.choose(0, 23)
    minute <- Gen.choose(0, 59)
  } yield LocalDateTime.of(year, month, day, hour, minute)
}
