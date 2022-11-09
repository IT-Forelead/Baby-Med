package babymed.services.payments.generators

import org.scalacheck.Gen

import babymed.services.payments.domain.CreatePayment
import babymed.services.payments.domain.Payment
import babymed.services.payments.domain.PaymentFilters
import babymed.services.payments.domain.PaymentWithCustomer
import babymed.services.users.generators.CustomerGenerators
import babymed.services.users.generators.UserGenerators

trait PaymentGenerator extends TypeGen with UserGenerators with CustomerGenerators {
  val paymentGen: Gen[Payment] =
    for {
      id <- paymentIdGen
      createdAt <- localDateTimeGen
      customerId <- customerIdGen
      price <- priceGen
    } yield Payment(id, createdAt, customerId, price)

  val createPaymentGen: Gen[CreatePayment] =
    for {
      customerId <- customerIdGen
      price <- priceGen
    } yield CreatePayment(customerId, price)

  val paymentWithCustomerGen: Gen[PaymentWithCustomer] =
    for {
      payment <- paymentGen
      customer <- customerGen
    } yield PaymentWithCustomer(payment, customer)

  val searchFiltersGen: Gen[PaymentFilters] =
    for {
      startDate <- localDateTimeGen.opt
      endDate <- localDateTimeGen.opt
      limit <- nonNegIntGen.opt
      page <- nonNegIntGen.opt
    } yield PaymentFilters(startDate, endDate, limit, page)
}
