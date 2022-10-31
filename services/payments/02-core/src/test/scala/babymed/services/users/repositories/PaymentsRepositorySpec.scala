package babymed.services.users.repositories

import babymed.services.payments.generators.PaymentGenerator
import babymed.services.payments.repositories.PaymentsRepository
import babymed.services.payments.domain.SearchFilters
import babymed.services.users.domain.types.CustomerId
import babymed.test.DBSuite

import java.time.LocalDateTime
import java.util.UUID

object PaymentsRepositorySpec extends DBSuite with PaymentGenerator{

  test("Create Customer") { implicit postgres =>
    val repo = PaymentsRepository.make[F]
    val defaultCustomerId = CustomerId(UUID.fromString("f4484324-e6cd-4e48-8d24-638f0a4fabaa"))
    repo
      .create(createPaymentGen.get.copy(customerId = defaultCustomerId))
      .map { c =>
        assert(c.createdAt.isBefore(LocalDateTime.now()))
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Get Payments") { implicit postgres =>
    val repo = PaymentsRepository.make[F]
    val defaultCustomerId = CustomerId(UUID.fromString("f4484324-e6cd-4e48-8d24-638f0a4fabaa"))
    val createPaymentData = createPaymentGen.get
    repo.create(createPaymentData.copy(customerId = defaultCustomerId)) *>
      repo
        .get(SearchFilters.Empty)
        .map { payments =>
          assert(payments.exists(_.price == createPaymentData.price))
        }
        .handleError {
          fail("Test failed.")
        }
  }

}
