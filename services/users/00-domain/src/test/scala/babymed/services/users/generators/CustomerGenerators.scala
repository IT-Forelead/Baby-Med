package babymed.services.users.generators

import org.scalacheck.Gen

import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.Customer
import babymed.services.users.domain.CustomerWithAddress
import babymed.services.users.domain.Region
import babymed.services.users.domain.Town
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId

trait CustomerGenerators extends TypeGen {
  lazy val customerGen: Gen[Customer] =
    for {
      id <- customerIdGen
      createdAt <- localDateTimeGen
      firstname <- firstNameGen
      lastname <- lastNameGen
      regionId <- regionIdGen
      townId <- townIdGen
      address <- addressGen
      birthday <- dateGen
      phone <- phoneGen
    } yield Customer(
      id,
      createdAt,
      firstname,
      lastname,
      regionId,
      townId,
      address,
      birthday,
      phone,
    )

  def createCustomerGen(
      maybeRegionId: Option[RegionId] = None,
      maybeTownId: Option[TownId] = None,
    ): Gen[CreateCustomer] =
    for {
      firstname <- firstNameGen
      lastname <- lastNameGen
      regionId <- regionIdGen
      townId <- townIdGen
      address <- addressGen
      birthday <- dateGen
      phone <- phoneGen
    } yield CreateCustomer(
      firstname,
      lastname,
      maybeRegionId.getOrElse(regionId),
      maybeTownId.getOrElse(townId),
      address,
      birthday,
      phone,
    )

  lazy val regionGen: Gen[Region] =
    for {
      id <- regionIdGen
      name <- regionNameGen
    } yield Region(id, name)

  lazy val townGen: Gen[Town] =
    for {
      id <- townIdGen
      regionId <- regionIdGen
      name <- townNameGen
    } yield Town(id, regionId, name)

  lazy val customerWithAddressGen: Gen[CustomerWithAddress] =
    for {
      customer <- customerGen
      region <- regionGen
      town <- townGen
    } yield CustomerWithAddress(customer, region, town)
}
