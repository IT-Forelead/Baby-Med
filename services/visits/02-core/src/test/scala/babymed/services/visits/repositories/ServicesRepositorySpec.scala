package babymed.services.visits.repositories

import babymed.services.visits.domain.CreateService
import babymed.services.visits.generators.ServiceGenerators
import babymed.support.database.DBSuite

object ServicesRepositorySpec extends DBSuite with ServiceGenerators {
  override def schemaName: String = "public"

  test("Create Service") { implicit postgres =>
    val create = createServiceGen.get
    ServicesRepository
      .make[F]
      .create(create)
      .map { s =>
        assert(s.name == create.name)
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Get All Services") { implicit postgres =>
    val repo = ServicesRepository.make[F]
    val create: CreateService = createServiceGen.get

    repo.create(create) *>
      repo
        .get
        .map { services =>
          assert(services.exists(_.name == create.name))
        }
        .handleError { error =>
          println("ERROR::::::::::::::::::: " + error)
          failure("Test failed.")
        }
  }
}
