package babymed.services.users.boundary

import cats.effect.kernel.Sync
import org.scalacheck.Gen
import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.Customer
import babymed.services.users.domain.CustomerWithAddress
import babymed.services.users.domain.Region
import babymed.services.users.domain.SearchFilters
import babymed.services.users.domain.Town
import babymed.services.users.domain.types
import babymed.services.users.domain.types.CustomerId
import babymed.services.users.generators.CustomerGenerators
import babymed.services.users.repositories.CustomersRepository
import babymed.test.TestSuite

object CustomersSpec extends TestSuite with CustomerGenerators {
  val customerRepo: CustomersRepository[F] = new CustomersRepository[F] {
    override def create(createCustomer: CreateCustomer): F[Customer] =
      Sync[F].delay(customerGen.get)

    override def getCustomerById(customerId: CustomerId): F[Option[CustomerWithAddress]] =
      Sync[F].delay(customerWithAddressGen.getOpt)

    override def get(filters: SearchFilters): F[List[CustomerWithAddress]] =
      Sync[F].delay(List(customerWithAddressGen.get))

    override def getTotal(filters: SearchFilters): F[Long] =
      Sync[F].delay(Gen.long.get)

    override def getRegions: F[List[Region]] =
      Sync[F].delay(List(regionGen.get))

    override def getTownsByRegionId(regionId: types.RegionId): F[List[Town]] =
      Sync[F].delay(List(townGen.get))
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

  loggedTest("Get Customers by Id") { logger =>
    customers
      .getCustomerById(customerIdGen.get)
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

  loggedTest("Get All Regions") { logger =>
    customers
      .getRegions
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Towns by RegionId") { logger =>
    customers
      .getTownsByRegionId(regionIdGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }
}
