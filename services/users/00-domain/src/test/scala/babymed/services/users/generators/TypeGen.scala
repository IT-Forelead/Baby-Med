package babymed.services.users.generators

import org.scalacheck.Gen

import babymed.domain.Role
import babymed.refinements.Password
import babymed.services.users.domain.types._
import babymed.syntax.refined.commonSyntaxAutoRefineV
import babymed.test.generators.Generators

trait TypeGen extends Generators {
  val userIdGen: Gen[UserId] = idGen(UserId.apply)
  val regionIdGen: Gen[RegionId] = idGen(RegionId.apply)
  val townIdGen: Gen[TownId] = idGen(TownId.apply)
  val addressGen: Gen[Address] = nonEmptyString.map(Address(_))
  val customerIdGen: Gen[CustomerId] = idGen(CustomerId.apply)
  val firstNameGen: Gen[FirstName] = nonEmptyString.map(FirstName(_))
  val lastNameGen: Gen[LastName] = nonEmptyString.map(LastName(_))
  val roleGen: Gen[Role] = Gen.oneOf(Role.values)
  val regionNameGen: Gen[RegionName] = nonEmptyString.map(RegionName(_))
  val townNameGen: Gen[TownName] = nonEmptyString.map(TownName(_))

  lazy val passwordGen: Gen[Password] = for {
    s1 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s2 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s3 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s4 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s5 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
    s6 <- Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
  } yield s"$s1$s2$s3$s4$s5$s6"
}
