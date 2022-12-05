package babymed.services.users.boundary

import cats.effect.kernel.Sync
import org.scalacheck.Gen

import babymed.services.users.domain._
import babymed.services.users.domain.types.Fullname
import babymed.services.users.domain.types.PatientId
import babymed.services.users.generators.PatientGenerators
import babymed.services.users.repositories.PatientsRepository
import babymed.test.TestSuite

object PatientsSpec extends TestSuite with PatientGenerators {
  val patientRepo: PatientsRepository[F] = new PatientsRepository[F] {
    override def create(createPatient: CreatePatient): F[Patient] =
      Sync[F].delay(patientGen.get)

    override def getPatientById(patientId: PatientId): F[Option[PatientWithAddress]] =
      Sync[F].delay(patientWithAddressGen.getOpt)

    override def get(filters: PatientFilters): F[List[PatientWithAddress]] =
      Sync[F].delay(List(patientWithAddressGen.get))

    override def getTotal(filters: PatientFilters): F[Long] =
      Sync[F].delay(Gen.long.get)

    override def getRegions: F[List[Region]] =
      Sync[F].delay(List(regionGen.get))

    override def getCitiesByRegionId(regionId: types.RegionId): F[List[City]] =
      Sync[F].delay(List(cityGen.get))

    override def getPatientsByName(name: Fullname): F[List[PatientWithName]] =
      ???
  }

  val patients: Patients[F] = new Patients[F](patientRepo)

  loggedTest("Create Patient") { logger =>
    patients
      .create(createPatientGen().get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Patients") { logger =>
    patients
      .getPatients(PatientFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Patients by Id") { logger =>
    patients
      .getPatientById(patientIdGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get patients Total") { logger =>
    patients
      .getTotalPatients(PatientFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get All Regions") { logger =>
    patients
      .getRegions
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Cities by RegionId") { logger =>
    patients
      .getCitiesByRegionId(regionIdGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }
}
