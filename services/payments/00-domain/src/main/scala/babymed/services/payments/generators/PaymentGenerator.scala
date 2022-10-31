package babymed.services.payments.generators
import babymed.services.payments.domain.{CreatePayment, Payment, PaymentWithCustomer, SearchFilters}
import babymed.services.users.domain.Customer
import org.scalacheck.Gen

trait PaymentGenerator extends TypeGen with UsersTypeGen {
  def paymentGen: Gen[Payment] =
    Payment(
      id = paymentIdGen.get,
      createdAt = localDateTimeGen.get,
      customerId = customerIdGen.get,
      price = priceGen.get
    )

  def createPaymentGen: Gen[CreatePayment] =
    CreatePayment(
      customerId = customerIdGen.get,
      price = priceGen.get
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

  def paymentWithCustomerGen: Gen[PaymentWithCustomer] =
    PaymentWithCustomer(
      payment = paymentGen.get,
      customer = customerGen.get
    )

  def searchFiltersGen: Gen[SearchFilters] =
    SearchFilters(
      startDate = localDateTimeGen.getOpt,
      endDate = localDateTimeGen.getOpt,
      limit = nonNegIntGen.getOpt,
      page = nonNegIntGen.getOpt
    )
}
