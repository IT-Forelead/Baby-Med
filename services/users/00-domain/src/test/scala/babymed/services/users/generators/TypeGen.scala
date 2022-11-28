package babymed.services.users.generators

import org.scalacheck.Gen

import babymed.domain.Role
import babymed.refinements.Password
import babymed.services.users.domain.types._
import babymed.syntax.refined.commonSyntaxAutoRefineV
import babymed.test.generators.Generators

trait TypeGen extends Generators {
  val passwordChars: Gen[Char] =
    Gen.oneOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz")
  val userIdGen: Gen[UserId] = idGen(UserId.apply)
  val regionIdGen: Gen[RegionId] = idGen(RegionId.apply)
  val cityIdGen: Gen[CityId] = idGen(CityId.apply)
  val subRoleIdGen: Gen[SubRoleId] = idGen(SubRoleId.apply)
  val addressGen: Gen[Address] = nonEmptyString.map(Address(_))
  val patientIdGen: Gen[PatientId] = idGen(PatientId.apply)
  val firstNameGen: Gen[FirstName] = nonEmptyString.map(FirstName(_))
  val lastNameGen: Gen[LastName] = nonEmptyString.map(LastName(_))
  val roleGen: Gen[Role] = Gen.oneOf(Role.values)
  val regionNameGen: Gen[RegionName] = nonEmptyString.map(RegionName(_))
  val cityNameGen: Gen[CityName] = nonEmptyString.map(CityName(_))

  lazy val passwordGen: Gen[Password] =
    Gen.listOfN(6, passwordChars).map(_.mkString)
}
