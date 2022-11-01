package babymed.services.users.boundary

import babymed.services.payments.boundary.Payments
import babymed.services.payments.domain.{CreatePayment, Payment, PaymentWithCustomer, SearchFilters}
import babymed.services.payments.repositories.PaymentsRepository
import babymed.services.payments.generators.PaymentGenerator
import babymed.test.TestSuite
import cats.effect.kernel.Sync
import org.scalacheck.Gen

object PaymentsSpec extends TestSuite with PaymentGenerator {
  val paymentRepo: PaymentsRepository[F] = new PaymentsRepository[F] {
    override def create(createPayment: CreatePayment): F[Payment] =
      Sync[F].delay(paymentGen.get)
    override def get(searchFilters: SearchFilters): F[List[PaymentWithCustomer]] =
      Sync[F].delay(List(paymentWithCustomerGen.get))
    override def getPaymentsTotal(filters: SearchFilters): PaymentsSpec.F[Long] =
      Sync[F].delay(Gen.long.get)
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

  loggedTest("Get Payments Total") { logger =>
    payments
      .getPaymentsTotal(SearchFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

}
