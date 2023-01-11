package babymed.services.visits.boundary

import cats.effect.kernel.Sync
import org.scalacheck.Gen

import babymed.services.visits.domain.CheckupExpense
import babymed.services.visits.domain.CheckupExpenseFilters
import babymed.services.visits.domain.CheckupExpenseInfo
import babymed.services.visits.domain.CreateCheckupExpense
import babymed.services.visits.domain.CreateDoctorShare
import babymed.services.visits.domain.CreateOperationExpense
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.DoctorShare
import babymed.services.visits.domain.DoctorShareInfo
import babymed.services.visits.domain.OperationExpense
import babymed.services.visits.domain.OperationExpenseFilters
import babymed.services.visits.domain.OperationExpenseInfo
import babymed.services.visits.domain.OperationExpenseItemWithUser
import babymed.services.visits.domain.OperationFilters
import babymed.services.visits.domain.OperationInfo
import babymed.services.visits.domain.OperationService
import babymed.services.visits.domain.OperationServiceInfo
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.domain.PatientVisitReport
import babymed.services.visits.domain.VisitItem
import babymed.services.visits.domain.types.DoctorShareId
import babymed.services.visits.domain.types.OperationExpenseId
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.domain.types.ServiceTypeId
import babymed.services.visits.generators.CheckupExpenseGenerators
import babymed.services.visits.generators.OperationExpenseGenerators
import babymed.services.visits.generators.PatientVisitGenerators
import babymed.services.visits.repositories.CheckupExpensesRepository
import babymed.services.visits.repositories.OperationExpensesRepository
import babymed.services.visits.repositories.VisitsRepository
import babymed.test.TestSuite

object VisitsSpec
    extends TestSuite
       with PatientVisitGenerators
       with CheckupExpenseGenerators
       with OperationExpenseGenerators {
  lazy val patientVisit: PatientVisit = patientVisitGen.get
  val visitRepo: VisitsRepository[F] = new VisitsRepository[F] {
    override def get(filters: PatientVisitFilters): F[List[PatientVisitReport]] =
      Sync[F].delay(List(patientVisitReportGen.get))
    override def create(createPatientVisit: CreatePatientVisit): F[PatientVisit] =
      Sync[F].delay(patientVisit)
    override def getTotal(filters: PatientVisitFilters): F[Long] =
      Sync[F].delay(Gen.long.get)
    override def updatePaymentStatus(id: PatientVisitId): F[PatientVisit] =
      Sync[F].delay(patientVisit)
    override def getItemsByVisitId(visitId: PatientVisitId): F[List[VisitItem]] =
      Sync[F].delay(List(visitItemGen.get))
    override def getVisitsByServiceTypeId(
        serviceTypeId: ServiceTypeId
      ): F[List[PatientVisitReport]] =
      ???
    override def getVisitById(id: PatientVisitId): F[PatientVisit] =
      Sync[F].delay(patientVisit)
  }
  val checkupRepo: CheckupExpensesRepository[F] = new CheckupExpensesRepository[F] {
    override def createDoctorShare(createDoctorShare: CreateDoctorShare): F[DoctorShare] = ???
    override def get(filters: CheckupExpenseFilters): F[List[CheckupExpenseInfo]] = ???
    override def getTotal(filters: CheckupExpenseFilters): F[Long] = ???
    override def getDoctorShares: VisitsSpec.F[List[DoctorShareInfo]] = ???
    override def deleteDoctorShare(id: DoctorShareId): F[Unit] = ???
    override def create(
        createCheckupExpenses: List[CreateCheckupExpense]
      ): F[List[CheckupExpense]] =
      Sync[F].delay(List(checkupExpenseGen.get))
  }

  val operationRepo: OperationExpensesRepository[F] = new OperationExpensesRepository[F] {
    override def getItemsById(id: OperationExpenseId): F[List[OperationExpenseItemWithUser]] =
      ???
    override def getOperationsTotal(filters: OperationFilters): F[Long] = ???
    override def create(createOperationExpense: CreateOperationExpense): F[OperationExpense] =
      Sync[F].delay(operationExpenseGen.get)
    override def createOperationService(serviceId: ServiceId): F[OperationService] =
      ???
    override def getOperations(filters: OperationFilters): F[List[OperationInfo]] =
      ???
    override def getTotal(filters: OperationExpenseFilters): F[Long] = ???
    override def getOperationServices: F[List[OperationServiceInfo]] = ???
    override def createOperation(
        visit: PatientVisit,
        serviceIds: List[ServiceId],
      ): F[Unit] = Sync[F].unit
    override def get(filters: OperationExpenseFilters): F[List[OperationExpenseInfo]] =
      ???
  }

  val visits: Visits[F] = new Visits[F](visitRepo, checkupRepo, operationRepo)
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

  loggedTest("Get Patient Visits") { logger =>
    visits
      .get(PatientVisitFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Update Payment Status") { logger =>
    visits
      .updatePaymentStatus(patientVisit.id)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error(s"$error _______________Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }
}
