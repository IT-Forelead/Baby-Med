package babymed.services.visits.repositories

import java.time.LocalDateTime

import cats.effect.IO
import cats.effect.Resource
import cats.implicits.catsSyntaxOptionId
import cats.implicits.toTraverseOps
import skunk.Session
import weaver.Expectations

import babymed.services.visits.domain.OperationExpenseFilters
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
          .map { visits =>
            assert(visits.length == 3)
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
}
