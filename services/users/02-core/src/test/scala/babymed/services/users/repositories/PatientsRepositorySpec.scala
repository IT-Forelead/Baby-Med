package babymed.services.users.repositories

import java.time.LocalDateTime

import cats.effect.IO
import cats.effect.Resource
import cats.implicits.catsSyntaxOptionId
import cats.implicits.toTraverseOps
import skunk.Session
import weaver.Expectations

import babymed.services.users.domain.PatientFilters
import babymed.services.users.generators.PatientGenerators
import babymed.support.database.DBSuite
import babymed.syntax.refined.commonSyntaxAutoRefineV

object PatientsRepositorySpec extends DBSuite with PatientGenerators {
  override def schemaName: String = "public"
  override def beforeAll(implicit session: Res): IO[Unit] = data.setup

  test("Create Patient") { implicit postgres =>
    PatientsRepository
      .make[F]
      .create(createPatientGen(data.regions.id1.some, data.cities.id1.some).get)
      .map { c =>
        assert(c.createdAt.isBefore(LocalDateTime.now()))
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Get Patient by Id") { implicit postgres =>
    PatientsRepository
      .make[F]
      .getPatientById(data.patient.id1)
      .map { patients =>
        assert(patients.exists(_.patient.id == data.patient.id1))
      }
      .handleError { error =>
        println("ERROR::::::::::::::::::: " + error)
        failure("Test failed.")
      }
  }

  test("Get Patients") { implicit postgres =>
    val repo = PatientsRepository.make[F]
    object Case1 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(PatientFilters(patientFirstName = data.patient.data1.firstname.some))
          .map { patients =>
            assert(patients.map(_.patient.id) == List(data.patient.id1))
          }
    }
    object Case2 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(PatientFilters(regionId = data.regions.id2.some))
          .map { patients =>
            assert.same(patients.map(_.patient.id), data.patient.values.keys.toList)
          }
    }
    object Case3 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(PatientFilters(cityId = data.cities.id2.some))
          .map { patients =>
            assert.same(patients.map(_.patient.id), data.patient.values.keys.toList)
          }
    }
    object Case4 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(PatientFilters(phone = data.patient.data1.phone.some))
          .map { patients =>
            assert.same(patients.map(_.patient.id), List(data.patient.id1))
          }
    }
    object Case5 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(PatientFilters(phone = Some("+998990123456")))
          .map { patients =>
            assert(patients.isEmpty)
          }
    }
    object Case6 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(
            PatientFilters(
              startDate = LocalDateTime.now().minusMinutes(1).some,
              regionId = data.regions.id2.some,
            )
          )
          .map { patients =>
            assert(patients.length == 3)
          }
    }
    object Case7 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(PatientFilters(endDate = LocalDateTime.now().minusMinutes(1).some))
          .map { patients =>
            assert(patients.isEmpty)
          }
    }
    List(
      Case1,
      Case2,
      Case3,
      Case4,
      Case5,
      Case6,
      Case7,
    ).traverse(_.check).map(_.reduce(_ and _))
  }

  test("Get Patient Total") { implicit postgres =>
    PatientsRepository
      .make[F]
      .getTotal(PatientFilters.Empty)
      .map { total =>
        assert(total >= 1)
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Get All Regions") { implicit postgres =>
    PatientsRepository
      .make[F]
      .getRegions
      .map { regions =>
        assert(regions.nonEmpty)
      }
      .handleError { error =>
        println("ERROR::::::::::::::::::: " + error)
        failure("Test failed.")
      }
  }

  test("Get Cities by RegionId") { implicit postgres =>
    PatientsRepository
      .make[F]
      .getCitiesByRegionId(data.regions.id1)
      .map { cities =>
        assert(cities.nonEmpty)
      }
      .handleError { error =>
        println("ERROR::::::::::::::::::: " + error)
        failure("Test failed.")
      }
  }
}
