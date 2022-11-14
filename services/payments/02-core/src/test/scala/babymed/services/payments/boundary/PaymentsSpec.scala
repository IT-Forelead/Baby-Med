package babymed.services.payments.boundary

import cats.effect.kernel.Sync
import org.scalacheck.Gen

import babymed.services.payments.domain.CreatePayment
import babymed.services.payments.domain.Payment
import babymed.services.payments.domain.PaymentFilters
import babymed.services.payments.domain.PaymentWithPatient
import babymed.services.payments.domain.types.PaymentId
import babymed.services.payments.generators.PaymentGenerator
import babymed.services.payments.repositories.PaymentsRepository
import babymed.test.TestSuite

object PaymentsSpec extends TestSuite with PaymentGenerator {
  val paymentRepo: PaymentsRepository[F] = new PaymentsRepository[F] {
    override def create(createPayment: CreatePayment): F[Payment] =
      Sync[F].delay(paymentGen.get)
    override def get(searchFilters: PaymentFilters): F[List[PaymentWithPatient]] =
      Sync[F].delay(List(paymentWithPatientGen.get))
    override def getPaymentsTotal(filters: PaymentFilters): F[Long] =
      Sync[F].delay(Gen.long.get)
    override def delete(paymentId: PaymentId): F[Unit] = Sync[F].unit
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
      .getPaymentsTotal(PaymentFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Delete Payment") { logger =>
    payments
      .delete(paymentIdGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }
}
