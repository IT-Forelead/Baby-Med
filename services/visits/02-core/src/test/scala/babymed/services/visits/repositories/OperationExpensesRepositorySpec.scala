package babymed.services.visits.repositories

import java.time.LocalDateTime

import cats.effect.IO
import cats.effect.Resource
import cats.implicits.catsSyntaxOptionId
import cats.implicits.toTraverseOps
import skunk.Session
import weaver.Expectations

import babymed.services.visits.domain.OperationExpenseFilters
import babymed.services.visits.domain.OperationFilters
import babymed.services.visits.generators.OperationExpenseGenerators
import babymed.support.database.DBSuite

object OperationExpensesRepositorySpec extends DBSuite with OperationExpenseGenerators {
  override def schemaName: String = "public"
  override def beforeAll(implicit session: Res): IO[Unit] = data.setup

  test("Get Operation Expenses") { implicit postgres =>
    val repo = OperationExpensesRepository.make[F]
    object Case1 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(OperationExpenseFilters(endDate = LocalDateTime.now().minusMinutes(1).some))
          .map { operationExpense =>
            assert(operationExpense.isEmpty)
          }
    }
    object Case2 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(OperationExpenseFilters(startDate = LocalDateTime.now().minusMinutes(1).some))
          .map { operationExpenses =>
            assert(operationExpenses.length == 3)
          }
    }
    List(
      Case1,
      Case2,
    ).traverse(_.check).map(_.reduce(_ and _))
  }

  test("Get Operation Expenses Total") { implicit postgres =>
    OperationExpensesRepository
      .make[F]
      .getTotal(OperationExpenseFilters.Empty)
      .map { total =>
        assert(total == 3)
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Get Operations") { implicit postgres =>
    val repo = OperationExpensesRepository.make[F]
    object Case1 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .getOperations(OperationFilters(endDate = LocalDateTime.now().minusMinutes(1).some))
          .map { operationExpense =>
            assert(operationExpense.isEmpty)
          }
    }
    object Case2 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .getOperations(OperationFilters(startDate = LocalDateTime.now().minusMinutes(1).some))
          .map { operationExpenses =>
            assert(operationExpenses.length == 3)
          }
    }
    object Case3 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .getOperations(OperationFilters(patientId = data.patient.id1.some))
          .map { visitsReport =>
            assert(visitsReport.map(_.operation.patientId).contains(data.patient.id1))
          }
    }
    object Case4 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .getOperations(OperationFilters(serviceId = data.service.id1.some))
          .map { visitsReport =>
            assert(visitsReport.map(_.operation.serviceId).contains(data.service.id1))
          }
    }
    List(
      Case1,
      Case2,
      Case3,
      Case4,
    ).traverse(_.check).map(_.reduce(_ and _))
  }

  test("Get Operations Total") { implicit postgres =>
    OperationExpensesRepository
      .make[F]
      .getOperationsTotal(OperationFilters.Empty)
      .map { total =>
        assert(total == 3)
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Create Operation Service") { implicit postgres =>
    OperationExpensesRepository
      .make[F]
      .createOperationService(data.service.id1)
      .map { c =>
        assert(c.serviceId == data.service.id1)
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Get All Operation Services") { implicit postgres =>
    OperationExpensesRepository
      .make[F]
      .getOperationServices
      .map { operationServices =>
        assert(operationServices.nonEmpty)
      }
      .handleError { error =>
        println("ERROR::::::::::::::::::: " + error)
        failure("Test failed.")
      }
  }
}
