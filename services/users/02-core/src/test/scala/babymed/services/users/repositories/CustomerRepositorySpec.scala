package babymed.services.users.repositories

import java.time.LocalDateTime

import babymed.services.users.domain.Customer
import babymed.services.users.domain.SearchFilters
import babymed.services.users.generators.CustomerGenerators
import babymed.test.DBSuite
import cats.effect.IO

object CustomerRepositorySpec extends DBSuite with CustomerGenerators {
  val createCustomer: Customer.CreateCustomer = createCustomerGen.get

  test("Create Customer") { implicit postgres =>
    val repo = CustomersRepository.make[F]
    repo
      .create(createCustomer)
      .map { c =>
        assert(c.createdAt.isBefore(LocalDateTime.now()))
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Get Customer") { implicit postgres =>
    val repo = CustomersRepository.make[F]
    repo.create(createCustomer) *>
      repo
        .get(SearchFilters.Empty)
        .map { customers =>
          assert(customers.exists(_.phone == createCustomer.phone))
        }
        .handleError {
          fail("Test failed.")
        }
  }

  test("Get Customer Total") { implicit postgres =>
    val repo = CustomersRepository.make[F]
    repo.create(createCustomer) *>
      repo
        .getTotal(SearchFilters.Empty)
        .map { total =>
          assert(total >= 1)
        }
        .handleError {
          fail("Test failed.")
        }
  }
}
