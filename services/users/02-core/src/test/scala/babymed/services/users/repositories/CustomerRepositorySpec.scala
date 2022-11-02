package babymed.services.users.repositories

import java.time.LocalDateTime
import java.util.UUID

import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.SearchFilters
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId
import babymed.services.users.generators.CustomerGenerators
import babymed.test.DBSuite

object CustomerRepositorySpec extends DBSuite with CustomerGenerators {
  test("Create Customer") { implicit postgres =>
    val repo = CustomersRepository.make[F]
    val createCustomer: CreateCustomer = createCustomerGen.get
    val defaultRegionId: RegionId =
      RegionId(UUID.fromString("ad514b71-3096-4be5-a455-d87abbb081b2"))
    val defaultTownId: TownId = TownId(UUID.fromString("0d073b76-08ce-4b78-a88c-a0cb6f80eaf9"))

    repo
      .create(createCustomer.copy(regionId = defaultRegionId, townId = defaultTownId))
      .map { c =>
        assert(c.createdAt.isBefore(LocalDateTime.now()))
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Get Customer by Id") { implicit postgres =>
    val repo = CustomersRepository.make[F]
    val createCustomer: CreateCustomer = createCustomerGen.get
    val defaultRegionId: RegionId =
      RegionId(UUID.fromString("ad514b71-3096-4be5-a455-d87abbb081b2"))
    val defaultTownId: TownId = TownId(UUID.fromString("0d073b76-08ce-4b78-a88c-a0cb6f80eaf9"))

    repo.create(createCustomer.copy(regionId = defaultRegionId, townId = defaultTownId)).flatMap {
      customer =>
        repo
          .getCustomerById(customer.id)
          .map { customers =>
            assert(customers.exists(_.customer.id == customer.id))
          }
          .handleError { error =>
            println("ERROR::::::::::::::::::: " + error)
            failure("Test failed.")
          }
    }
  }

  test("Get Customers") { implicit postgres =>
    val repo = CustomersRepository.make[F]
    val createCustomer: CreateCustomer = createCustomerGen.get
    val defaultRegionId: RegionId =
      RegionId(UUID.fromString("ad514b71-3096-4be5-a455-d87abbb081b2"))
    val defaultTownId: TownId = TownId(UUID.fromString("0d073b76-08ce-4b78-a88c-a0cb6f80eaf9"))

    repo.create(createCustomer.copy(regionId = defaultRegionId, townId = defaultTownId)) *>
      repo
        .get(SearchFilters.Empty)
        .map { customers =>
          assert(customers.exists(_.customer.firstname == createCustomer.firstname))
        }
        .handleError { error =>
          println("ERROR::::::::::::::::::: " + error)
          failure("Test failed.")
        }
  }

  test("Get Customer Total") { implicit postgres =>
    val repo = CustomersRepository.make[F]
    val createCustomer: CreateCustomer = createCustomerGen.get
    val defaultRegionId: RegionId =
      RegionId(UUID.fromString("ad514b71-3096-4be5-a455-d87abbb081b2"))
    val defaultTownId: TownId = TownId(UUID.fromString("0d073b76-08ce-4b78-a88c-a0cb6f80eaf9"))

    repo.create(createCustomer.copy(regionId = defaultRegionId, townId = defaultTownId)) *>
      repo
        .getTotal(SearchFilters.Empty)
        .map { total =>
          assert(total >= 1)
        }
        .handleError {
          fail("Test failed.")
        }
  }

  test("Get All Regions") { implicit postgres =>
    val repo = CustomersRepository.make[F]
    repo
      .getRegions
      .map { regions =>
        assert(regions.nonEmpty)
      }
      .handleError { error =>
        println("ERROR::::::::::::::::::: " + error)
        failure("Test failed.")
      }
  }

  test("Get Towns by RegionId") { implicit postgres =>
    val repo = CustomersRepository.make[F]
    val defaultRegionId: RegionId =
      RegionId(UUID.fromString("ad514b71-3096-4be5-a455-d87abbb081b2"))
    repo
      .getTownsByRegionId(defaultRegionId)
      .map { towns =>
        assert(towns.nonEmpty)
      }
      .handleError { error =>
        println("ERROR::::::::::::::::::: " + error)
        failure("Test failed.")
      }
  }
}
