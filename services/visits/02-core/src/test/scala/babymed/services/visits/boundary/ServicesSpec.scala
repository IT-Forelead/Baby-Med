package babymed.services.visits.boundary

import cats.effect.kernel.Sync

import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.Service
import babymed.services.visits.generators.ServiceGenerators
import babymed.services.visits.repositories.ServicesRepository
import babymed.test.TestSuite

object ServicesSpec extends TestSuite with ServiceGenerators {
  val serviceRepo: ServicesRepository[F] = new ServicesRepository[F] {
    override def create(createService: CreateService): F[Service] =
      Sync[F].delay(serviceGen.get)
    override def get: F[List[Service]] =
      Sync[F].delay(List(serviceGen.get))
  }

  val services: Services[F] = new Services[F](serviceRepo)

  loggedTest("Create Service") { logger =>
    services
      .create(createServiceGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get All Services") { logger =>
    services
      .get
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }
}
