package babymed.services.users.boundary

import babymed.services.payments.boundary.Payments
import babymed.services.payments.domain.{CreatePayment, Payment, SearchFilters}
import babymed.services.payments.repositories.PaymentsRepository
import babymed.services.payments.generators.PaymentGenerator
import babymed.test.TestSuite
import cats.effect.kernel.Sync

object PaymentsSpec extends TestSuite with PaymentGenerator {
  val paymentRepo: PaymentsRepository[F] = new PaymentsRepository[F] {
    override def create(createPayment: CreatePayment): F[Payment] =
      Sync[F].delay(paymentGen.get)
    override def get(searchFilters: SearchFilters): F[List[Payment]] =
      Sync[F].delay(List(paymentGen.get))
  }

  val payments: Payments[F] = new Payments[F](paymentRepo)

  loggedTest("Create Payment") { logger =>
    payments
      .create(createPaymentGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Payments by Filters") { implicit logger =>
    payments
      .get(searchFiltersGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

}
