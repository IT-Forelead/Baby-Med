package babymed.services.payments.generators
import babymed.services.payments.domain.{CreatePayment, Payment, SearchFilters}
import org.scalacheck.Gen

trait PaymentGenerator extends TypeGen {
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

  def searchFiltersGen: Gen[SearchFilters] =
    SearchFilters(
      startDate = localDateTimeGen.getOpt,
      endDate = localDateTimeGen.getOpt,
      limit = nonNegIntGen.getOpt,
      page = nonNegIntGen.getOpt
    )
}
