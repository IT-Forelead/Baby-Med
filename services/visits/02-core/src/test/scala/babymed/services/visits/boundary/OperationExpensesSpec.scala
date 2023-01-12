package babymed.services.visits.boundary

import cats.effect.kernel.Sync
import org.scalacheck.Gen

import babymed.services.visits.domain._
import babymed.services.visits.domain.types.OperationExpenseId
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.generators.OperationExpenseGenerators
import babymed.services.visits.repositories.OperationExpensesRepository
import babymed.test.TestSuite

object OperationExpensesSpec extends TestSuite with OperationExpenseGenerators {
  val operationExpenseRepo: OperationExpensesRepository[F] = new OperationExpensesRepository[F] {
    override def create(createOperationExpense: CreateOperationExpense): F[OperationExpense] =
      Sync[F].delay(operationExpenseGen.get)
    override def get(filters: OperationExpenseFilters): F[List[OperationExpenseInfo]] =
      Sync[F].delay(List(operationExpenseWithPatientVisitGen.get))
    override def getTotal(filters: OperationExpenseFilters): F[Long] =
      Sync[F].delay(Gen.long.get)
    override def getItemsById(id: OperationExpenseId): F[List[OperationExpenseItemWithUser]] =
      Sync[F].delay(List(operationExpenseItemWithUserGen.get))
    override def createOperation(
        visit: PatientVisit,
        serviceIds: List[ServiceId],
      ): F[Unit] = ???
    override def getOperations(filters: OperationFilters): F[List[OperationInfo]] =
      Sync[F].delay(List(operationInfoGen.get))
    override def getOperationsTotal(filters: OperationFilters): F[Long] =
      Sync[F].delay(Gen.long.get)
    override def createOperationService(serviceId: ServiceId): F[OperationService] =
      Sync[F].delay(operationServiceGen.get)
    override def getOperationServices: F[List[OperationServiceInfo]] =
      Sync[F].delay(List(operationServiceInfoGen.get))
  }

  val operationExpenses: OperationExpenses[F] = new OperationExpenses[F](operationExpenseRepo)
  val createService: CreateService = createServiceGen().get

  loggedTest("Create Operation Expense") { logger =>
    operationExpenses
      .create(createOperationExpenseGen().get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Operation Expenses") { logger =>
    operationExpenses
      .get(OperationExpenseFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Operation Expenses Total") { logger =>
    operationExpenses
      .getTotal(OperationExpenseFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Operation Expense Items by Id") { logger =>
    operationExpenses
      .getItemsById(operationExpenseIdGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Create Operation Service") { logger =>
    operationExpenses
      .createOperationService(serviceIdGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Operations") { logger =>
    operationExpenses
      .getOperations(OperationFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get All Operation Services") { logger =>
    operationExpenses
      .getOperationServices
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }
}
