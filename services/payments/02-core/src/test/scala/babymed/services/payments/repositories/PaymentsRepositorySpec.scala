package babymed.services.payments.repositories

import java.time.LocalDateTime

import cats.effect.IO

import babymed.services.payments.domain.PaymentFilters
import babymed.services.payments.generators.PaymentGenerator
import babymed.services.users.generators.CustomerGenerators
import babymed.services.users.generators.UserGenerators
import babymed.support.database.DBSuite

object PaymentsRepositorySpec
    extends DBSuite
       with PaymentGenerator
       with UserGenerators
       with CustomerGenerators {
  override def schemaName: String = "public"
  override def beforeAll(implicit session: Res): IO[Unit] = data.setup
  test("Create Payment") { implicit postgres =>
    val repo = PaymentsRepository.make[F]
    repo
      .create(createPaymentGen.get.copy(customerId = data.customer.id1))
      .map { c =>
        assert(c.createdAt.isBefore(LocalDateTime.now()))
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Get Payments") { implicit postgres =>
    val repo = PaymentsRepository.make[F]
    val createPaymentData = createPaymentGen.get
    repo.create(createPaymentData.copy(customerId = data.customer.id1)) *>
      repo
        .get(PaymentFilters.Empty)
        .map { payments =>
          assert(payments.exists(_.payment.price == createPaymentData.price))
        }
        .handleError {
          fail("Test failed.")
        }
  }

  test("Get Payment Total") { implicit postgres =>
    val repo = PaymentsRepository.make[F]
    val createPaymentData = createPaymentGen.get

    repo.create(createPaymentData.copy(customerId = data.customer.id1)) *>
      repo
        .getPaymentsTotal(PaymentFilters.Empty)
        .map { total =>
          assert(total >= 1)
        }
        .handleError {
          fail("Test failed.")
        }
  }

  test("Delete Payment") { implicit postgres =>
    val repo = PaymentsRepository.make[F]
    val createPaymentData = createPaymentGen.get

    for {
      createPayment <- repo.create(createPaymentData.copy(customerId = data.customer.id1))
      _ <- repo.create(createPaymentGen.get.copy(customerId = data.customer.id1))
      _ <- repo.delete(createPayment.id)
      payments <- repo.get(PaymentFilters.Empty)
    } yield assert(payments.exists(_.payment.id != createPayment.id))
  }
}
