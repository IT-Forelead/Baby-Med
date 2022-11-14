package babymed.services.payments.generators

import org.scalacheck.Gen

import babymed.services.payments.domain.CreatePayment
import babymed.services.payments.domain.Payment
import babymed.services.payments.domain.PaymentFilters
import babymed.services.payments.domain.PaymentWithPatient
import babymed.services.users.generators.PatientGenerators
import babymed.services.users.generators.UserGenerators

trait PaymentGenerator extends TypeGen with UserGenerators with PatientGenerators {
  val paymentGen: Gen[Payment] =
    for {
      id <- paymentIdGen
      createdAt <- localDateTimeGen
      patientId <- patientIdGen
      price <- priceGen
    } yield Payment(id, createdAt, patientId, price)

  val createPaymentGen: Gen[CreatePayment] =
    for {
      patientId <- patientIdGen
      price <- priceGen
    } yield CreatePayment(patientId, price)

  val paymentWithPatientGen: Gen[PaymentWithPatient] =
    for {
      payment <- paymentGen
      patient <- patientGen
    } yield PaymentWithPatient(payment, patient)

  val searchFiltersGen: Gen[PaymentFilters] =
    for {
      startDate <- localDateTimeGen.opt
      endDate <- localDateTimeGen.opt
      limit <- nonNegIntGen.opt
      page <- nonNegIntGen.opt
    } yield PaymentFilters(startDate, endDate, limit, page)
}
