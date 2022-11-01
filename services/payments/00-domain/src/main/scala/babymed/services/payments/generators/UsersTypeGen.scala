package babymed.services.payments.generators

import babymed.domain.Role
import babymed.services.users.domain.types._
import babymed.syntax.refined.commonSyntaxAutoRefineV
import babymed.test.generators.Generators
import org.scalacheck.Gen

trait UsersTypeGen extends Generators {
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
}
