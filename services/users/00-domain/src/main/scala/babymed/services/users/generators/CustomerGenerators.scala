package babymed.services.users.generators

import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.Customer
import babymed.services.users.domain.CustomerWithAddress
import babymed.services.users.domain.Region
import babymed.services.users.domain.Town
import org.scalacheck.Gen

trait CustomerGenerators extends TypeGen {
  def customerGen: Gen[Customer] =
    Customer(
      id = customerIdGen.get,
      createdAt = localDateTimeGen.get,
      firstname = firstNameGen.get,
      lastname = lastNameGen.get,
      regionId = regionIdGen.get,
      townId = townIdGen.get,
      address = addressGen.get,
      birthday = dateGen.get,
      phone = phoneGen.get,
    )

  def createCustomerGen: Gen[CreateCustomer] =
    CreateCustomer(
      firstname = firstNameGen.get,
      lastname = lastNameGen.get,
      regionId = regionIdGen.get,
      townId = townIdGen.get,
      address = addressGen.get,
      birthday = dateGen.get,
      phone = phoneGen.get,
    )

  def regionGen: Gen[Region] =
    Region(
      id = regionIdGen.get,
      name = regionNameGen.get,
    )

  def townGen: Gen[Town] =
    Town(
      id = townIdGen.get,
      regionId = regionIdGen.get,
      name = townNameGen.get,
    )

  def customerWithAddressGen: Gen[CustomerWithAddress] =
    CustomerWithAddress(
      customer = customerGen.get,
      region = regionGen.get,
      town = townGen.get,
    )
}
