package babymed.services.visits.repositories

import java.time.LocalDateTime

import cats.effect.IO
import cats.effect.Resource
import cats.implicits.catsSyntaxOptionId
import cats.implicits.toTraverseOps
import skunk.Session
import weaver.Expectations

import babymed.domain.PaymentStatus.FullyPaid
import babymed.domain.PaymentStatus.NotPaid
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.generators.PatientVisitGenerators
import babymed.support.database.DBSuite

object VisitsRepositorySpec extends DBSuite with PatientVisitGenerators {
  override def schemaName: String = "public"
  override def beforeAll(implicit session: Res): IO[Unit] = data.setup

  test("Create Patient Visit") { implicit postgres =>
    VisitsRepository
      .make[F]
      .create(
        createPatientVisitGen(
          data.patient.id1.some,
          data.user.id1.some,
          data.service.id1.some,
        ).get
      )
      .map { c =>
        assert(c.createdAt.isBefore(LocalDateTime.now()))
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Get Patient Visits") { implicit postgres =>
    val repo = VisitsRepository.make[F]
    object Case1 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(PatientVisitFilters(patientId = data.patient.id1.some))
          .map { visits =>
            assert(visits.map(_.patientVisit.patientId).contains(data.patient.id1))
          }
    }
    object Case2 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(PatientVisitFilters(userId = data.user.id1.some))
          .map { visits =>
            assert(visits.map(_.patientVisit.userId).contains(data.user.id1))
          }
    }
    object Case3 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(PatientVisitFilters(serviceId = data.service.id1.some))
          .map { visits =>
            assert(visits.map(_.patientVisit.serviceId).contains(data.service.id1))
          }
    }
    object Case4 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(PatientVisitFilters(paymentStatus = NotPaid.some))
          .map { visits =>
            assert.all(
              visits.forall(_.patientVisit.paymentStatus == NotPaid)
            )
          }
    }
    object Case5 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(PatientVisitFilters(startDate = LocalDateTime.now().minusMinutes(1).some))
          .map { visits =>
            assert(visits.length == 3)
          }
    }
    object Case6 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(PatientVisitFilters(endDate = LocalDateTime.now().minusMinutes(1).some))
          .map { visits =>
            assert(visits.isEmpty)
          }
    }
    List(
      Case1,
      Case2,
      Case3,
      Case4,
      Case5,
      Case6,
    ).traverse(_.check).map(_.reduce(_ and _))
  }

  test("Get Patient Visits Total") { implicit postgres =>
    VisitsRepository
      .make[F]
      .getTotal(PatientVisitFilters.Empty)
      .map { total =>
        assert(total >= 1)
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Update Payment Status") { implicit postgres =>
    val repo = VisitsRepository.make[IO]
    val create = createPatientVisitGen(
      data.patient.id2.some,
      data.user.id2.some,
      data.service.id2.some,
    ).get
    for {
      createVisit <- repo.create(create)
      _ <- repo.updatePaymentStatus(createVisit.id)
      visits <- repo.get(PatientVisitFilters(paymentStatus = FullyPaid.some))
    } yield assert(visits.exists(_.patientVisit.id == createVisit.id))
  }
}
