package babymed.services.users.repositories

import babymed.services.payments.generators.PaymentGenerator
import babymed.services.payments.repositories.PaymentsRepository
import babymed.test.DBSuite

import java.time.LocalDateTime

object PaymentsRepositorySpec extends DBSuite with PaymentGenerator{

  test("Create Customer") { implicit postgres =>
    val repo = PaymentsRepository.make[F]
    repo
      .create(createPaymentGen.get)
      .map { c =>
        assert(c.createdAt.isBefore(LocalDateTime.now()))
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Get Payments") { implicit postgres =>
      val repo = PaymentsRepository.make[F]
      repo.create(createPaymentGen.get) *>
        repo
          .get(searchFiltersGen.get)
          .map { payments =>
            assert(payments.exists(_.price == createPaymentGen.get.price))
          }
          .handleError {
            fail("Test failed.")
          }
  }

}
