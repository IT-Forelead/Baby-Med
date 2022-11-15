package babymed.services.visits.boundary

import cats.data.OptionT
import cats.effect.kernel.Sync
import cats.implicits.catsSyntaxOptionId
import babymed.services.visits.domain.{CreateService, EditService, Service, types}
import babymed.services.visits.generators.ServiceGenerators
import babymed.services.visits.repositories.ServicesRepository
import babymed.test.TestSuite

object ServicesSpec extends TestSuite with ServiceGenerators {
  val serviceRepo: ServicesRepository[F] = new ServicesRepository[F] {
    override def create(createService: CreateService): F[Service] =
      Sync[F].delay(serviceGen.get)
    override def get: F[List[Service]] =
      Sync[F].delay(List(serviceGen.get))
    override def edit(
        editService: EditService
      ): ServicesSpec.F[Unit] = Sync[F].unit
    override def delete(
        serviceId: types.ServiceId
      ): ServicesSpec.F[Unit] = Sync[F].unit
  }

  val services: Services[F] = new Services[F](serviceRepo)
  val createService: CreateService = createServiceGen.get

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

  loggedTest("Edit Service") { logger =>
    services
      .create(createService)
      .map(service => services.edit(editServiceGen(service.id.some).get))
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Delete Service") { logger =>
    services
      .create(createService)
      .map(service => services.delete(service.id))
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }
}
