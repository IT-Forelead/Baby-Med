package babymed.services.visits.boundary

import cats.effect.kernel.Sync
import cats.implicits.catsSyntaxOptionId
import babymed.services.visits.domain.{CreateService, EditService, Service, ServiceType}
import babymed.services.visits.domain.types.{ServiceId, ServiceTypeId, ServiceTypeName}
import babymed.services.visits.generators.ServiceGenerators
import babymed.services.visits.repositories.ServicesRepository
import babymed.test.TestSuite

object ServicesSpec extends TestSuite with ServiceGenerators {
  val serviceRepo: ServicesRepository[F] = new ServicesRepository[F] {
    override def create(createService: CreateService): F[Service] =
      Sync[F].delay(serviceGen.get)
    override def get(serviceTypeId: ServiceTypeId): F[List[Service]] =
      Sync[F].delay(List(serviceGen.get))
    override def edit(editService: EditService): F[Unit] = Sync[F].unit
    override def delete(serviceId: ServiceId): F[Unit] = Sync[F].unit
    override def createServiceType(name: ServiceTypeName): F[ServiceType] =
      Sync[F].delay(serviceTypeGen.get)
    override def getServiceTypes: F[List[ServiceType]] =
      Sync[F].delay(List(serviceTypeGen.get))
    override def deleteServiceType(id: ServiceTypeId): F[Unit] = Sync[F].unit
  }

  val services: Services[F] = new Services[F](serviceRepo)
  val createService: CreateService = createServiceGen().get

  loggedTest("Create Service Type") { logger =>
    services
      .createServiceType(serviceTypeNameGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get All Service Types") { logger =>
    services
      .getServiceTypes
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Delete Service Type") { logger =>
    val typeName: ServiceTypeName = serviceTypeNameGen.get
    services
      .createServiceType(typeName)
      .map(serviceType => services.deleteServiceType(serviceType.id))
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Create Service") { logger =>
    services
      .create(createServiceGen().get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Services by ServiceTypeId") { logger =>
    services
      .get(serviceTypeIdGen.get)
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
