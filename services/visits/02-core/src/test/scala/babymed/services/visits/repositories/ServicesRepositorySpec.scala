package babymed.services.visits.repositories

import cats.effect.IO
import cats.implicits.catsSyntaxOptionId

import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.types.ServiceTypeName
import babymed.services.visits.generators.ServiceGenerators
import babymed.support.database.DBSuite

object ServicesRepositorySpec extends DBSuite with ServiceGenerators {
  override def schemaName: String = "public"
  override def beforeAll(implicit session: Res): IO[Unit] = data.setup

  test("Create Service Type") { implicit postgres =>
    val typeName = serviceTypeNameGen.get
    ServicesRepository
      .make[F]
      .createServiceType(typeName)
      .map { s =>
        assert(s.name == typeName)
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Get All ServiceTypes") { implicit postgres =>
    val repo = ServicesRepository.make[F]
    val typeName = serviceTypeNameGen.get

    repo.createServiceType(typeName) *>
      repo
        .getServiceTypes
        .map { serviceTypes =>
          assert(serviceTypes.exists(_.name == typeName))
        }
        .handleError { error =>
          println("ERROR::::::::::::::::::: " + error)
          failure("Test failed.")
        }
  }

  test("Get All Services") { implicit postgres =>
    val repo = ServicesRepository.make[F]
    val create = createServiceGen(data.serviceType.id2.some).get

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

  test("Delete Service Type") { implicit postgres =>
    val repo = ServicesRepository.make[F]
    val typeName: ServiceTypeName = serviceTypeNameGen.get
    (for {
      serviceType <- repo.createServiceType(typeName)
      _ <- repo.deleteServiceType(serviceType.id)
      serviceTypes <- repo.getServiceTypes
    } yield assert(!serviceTypes.contains(serviceType)))
      .handleError { error =>
        println("ERROR::::::::::::::::::: " + error)
        failure("Test failed.")
      }
  }

  test("Create Service") { implicit postgres =>
    val create = createServiceGen(data.serviceType.id2.some).get
    ServicesRepository
      .make[F]
      .create(create)
      .map { s =>
        assert(s.name == create.name)
      }
      .handleError { error =>
        println("ERROR::::::::::::::::::: " + error)
        failure("Test failed.")
      }
  }

  test("Get Services by TypeId") { implicit postgres =>
    val repo = ServicesRepository.make[F]
    val create: CreateService = createServiceGen(data.serviceType.id1.some).get

    repo.create(create) *>
      repo
        .getServicesByTypeId(data.serviceType.id1)
        .map { services =>
          assert(services.exists(_.name == create.name))
        }
        .handleError { error =>
          println("ERROR::::::::::::::::::: " + error)
          failure("Test failed.")
        }
  }

  test("Update Service") { implicit postgres =>
    val repo = ServicesRepository.make[F]
    val create: CreateService = createServiceGen(data.serviceType.id2.some).get
    (for {
      service <- repo.create(create)
      editService = editServiceGen(service.id.some).get
      _ <- repo.edit(editService)
      services <- repo.getServicesByTypeId(data.serviceType.id2)
    } yield assert(services.exists(_.name == editService.name)))
      .handleError { error =>
        println("ERROR::::::::::::::::::: " + error)
        failure("Test failed.")
      }
  }

  test("Delete Service") { implicit postgres =>
    val repo = ServicesRepository.make[F]
    val create: CreateService = createServiceGen(data.serviceType.id3.some).get
    (for {
      service <- repo.create(create)
      _ <- repo.delete(service.id)
      services <- repo.getServicesByTypeId(data.serviceType.id3)
    } yield assert(!services.contains(service)))
      .handleError { error =>
        println("ERROR::::::::::::::::::: " + error)
        failure("Test failed.")
      }
  }
}
