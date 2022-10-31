package babymed.services.users.generators

import babymed.services.users.domain.Customer
import babymed.services.users.domain.Customer.CreateCustomer
import babymed.services.users.domain.Customer.CustomerWithAddress
import org.scalacheck.Gen

trait CustomerGenerators extends TypeGen {
  def customerWithAddressGen: Gen[CustomerWithAddress] =
    CustomerWithAddress(
      id = customerIdGen.get,
      createdAt = localDateTimeGen.get,
      firstname = firstNameGen.get,
      lastname = lastNameGen.get,
      regionId = regionIdGen.get,
      townId = townIdGen.get,
      address = addressGen.get,
      birthday = dateGen.get,
      phone = phoneGen.get,
      region = regionGen.get,
      town = townGen.get,
    )

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
}
