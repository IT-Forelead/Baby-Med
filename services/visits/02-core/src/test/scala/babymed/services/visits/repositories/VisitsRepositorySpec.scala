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
          .get(PatientVisitFilters(endDate = LocalDateTime.now().minusMinutes(1).some))
          .map { visits =>
            assert(visits.isEmpty)
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
            assert(
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
          .get(PatientVisitFilters(serviceTypeId = data.serviceType.id1.some))
          .map { visits =>
            assert(visits.exists(_.service.serviceTypeId == data.serviceType.id1))
          }
    }
    object Case7 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(PatientVisitFilters(userId = data.user.id1.some))
          .map { visits =>
            assert(visits.exists(_.patientVisit.userId == data.user.id1))
          }
    }
    object Case8 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(PatientVisitFilters(chequeId = data.visits.chequeId1.some))
          .map { visits =>
            assert(visits.exists(_.patientVisit.chequeId == data.visits.chequeId1))
          }
    }
    List(Case1, Case2, Case3, Case4, Case5, Case6, Case7, Case8)
      .traverse(_.check)
      .map(_.reduce(_ and _))
  }

  test("Get Patient Visits Total") { implicit postgres =>
    VisitsRepository
      .make[F]
      .getTotal(PatientVisitFilters.Empty)
      .map { total =>
        assert(total == 3)
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Update Payment Status") { implicit postgres =>
    val repo = VisitsRepository.make[IO]

    for {
      _ <- repo.updatePaymentStatus(data.visits.chequeId1)
      visits <- repo.get(PatientVisitFilters(paymentStatus = FullyPaid.some))
    } yield assert(visits.exists(_.patientVisit.id == data.visits.id1))
  }
}
