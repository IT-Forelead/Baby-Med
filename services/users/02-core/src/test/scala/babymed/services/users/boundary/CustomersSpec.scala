package babymed.services.users.boundary

import babymed.services.users.domain.Customer
import babymed.services.users.domain.Customer._
import babymed.services.users.domain.SearchFilters
import babymed.services.users.generators.CustomerGenerators
import babymed.services.users.repositories.CustomersRepository
import babymed.test.TestSuite
import cats.effect.kernel.Sync
import org.scalacheck.Gen

object CustomersSpec extends TestSuite with CustomerGenerators {
  val customerRepo: CustomersRepository[F] = new CustomersRepository[F] {
    override def create(createCustomer: CreateCustomer): F[Customer] = {
      Sync[F].delay(customerGen.get)
    }

    override def get(filters: SearchFilters): CustomersSpec.F[List[CustomerWithAddress]] =
      Sync[F].delay(List(customerWithAddressGen.get))

    override def getTotal(filters: SearchFilters): F[Long] =
      Sync[F].delay(Gen.long.get)
  }

  val customers: Customers[F] = new Customers[F](customerRepo)

  loggedTest("Create Customer") { logger =>
    customers
      .createCustomers(createCustomerGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Customers") { logger =>
    customers
      .getCustomers(SearchFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Customers Total") { logger =>
    customers
      .getTotalCustomers(SearchFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }
}
