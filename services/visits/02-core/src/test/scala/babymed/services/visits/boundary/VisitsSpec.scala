package babymed.services.visits.boundary

import cats.effect.kernel.Sync
import org.scalacheck.Gen

import babymed.services.visits.domain.CheckupExpense
import babymed.services.visits.domain.CheckupExpenseFilters
import babymed.services.visits.domain.CheckupExpenseInfo
import babymed.services.visits.domain.CreateDoctorShare
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.DoctorShare
import babymed.services.visits.domain.DoctorShareInfo
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.domain.PatientVisitInfo
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.generators.PatientVisitGenerators
import babymed.services.visits.repositories.CheckupExpensesRepository
import babymed.services.visits.repositories.VisitsRepository
import babymed.test.TestSuite

object VisitsSpec extends TestSuite with PatientVisitGenerators {
  val visitRepo: VisitsRepository[F] = new VisitsRepository[F] {
    override def create(createPatientVisit: CreatePatientVisit): F[PatientVisit] =
      Sync[F].delay(patientVisitGen.get)
    override def get(filters: PatientVisitFilters): F[List[PatientVisitInfo]] =
      Sync[F].delay(List(patientVisitInfoGen.get))
    override def getTotal(filters: PatientVisitFilters): F[Long] =
      Sync[F].delay(Gen.long.get)
    override def updatePaymentStatus(id: PatientVisitId): F[PatientVisit] =
      Sync[F].delay(patientVisitGen.get)
  }

  val checkupRepo: CheckupExpensesRepository[F] = new CheckupExpensesRepository[F] {
    override def create(serviceId: ServiceId): F[CheckupExpense] = ???
    override def createDoctorShare(createDoctorShare: CreateDoctorShare): F[DoctorShare] = ???
    override def get(filters: CheckupExpenseFilters): F[List[CheckupExpenseInfo]] = ???
    override def getTotal(filters: CheckupExpenseFilters): F[Long] = ???
    override def getDoctorShares: VisitsSpec.F[List[DoctorShareInfo]] = ???
  }

  val visits: Visits[F] = new Visits[F](visitRepo, checkupRepo)
  val createService: CreateService = createServiceGen().get

  loggedTest("Create Patient Visit") { logger =>
    visits
      .create(createPatientVisitGen().get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get All Patient Visits") { logger =>
    visits
      .get(PatientVisitFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Patient Visits Total") { logger =>
    visits
      .getTotal(PatientVisitFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Update Payment Status") { logger =>
    visits
      .create(createPatientVisitGen().get)
      .map(visit => visits.updatePaymentStatus(visit.id))
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }
}
