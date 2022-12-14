package babymed.services.visits.repositories

import java.time.LocalDateTime

import cats.effect.IO
import cats.effect.Resource
import cats.implicits.catsSyntaxOptionId
import cats.implicits.toTraverseOps
import skunk.Session
import weaver.Expectations

import babymed.services.visits.domain.CheckupExpenseFilters
import babymed.services.visits.generators.CheckupExpenseGenerators
import babymed.support.database.DBSuite

object CheckupExpensesRepositorySpec extends DBSuite with CheckupExpenseGenerators {
  override def schemaName: String = "public"
  override def beforeAll(implicit session: Res): IO[Unit] = data.setup

  test("Get Checkup Expenses") { implicit postgres =>
    val repo = CheckupExpensesRepository.make[F]
    object Case1 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(CheckupExpenseFilters(endDate = LocalDateTime.now().minusMinutes(1).some))
          .map { checkupExpense =>
            assert(checkupExpense.isEmpty)
          }
    }
    object Case2 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(CheckupExpenseFilters(startDate = LocalDateTime.now().minusMinutes(1).some))
          .map { checkupExpense =>
            assert(checkupExpense.length == 3)
          }
    }
    object Case3 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(CheckupExpenseFilters(serviceId = data.service.id2.some))
          .map { checkupExpense =>
            assert.same(checkupExpense.map(_.service.id), List(data.service.id2))
          }
    }
    object Case4 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(CheckupExpenseFilters(userId = data.user.id1.some))
          .map { checkupExpense =>
            assert.same(checkupExpense.map(_.user.id), List(data.user.id1))
          }
    }
    List(
      Case1,
      Case2,
      Case3,
      Case4,
    ).traverse(_.check).map(_.reduce(_ and _))
  }

  test("Get Checkup Expenses Total") { implicit postgres =>
    CheckupExpensesRepository
      .make[F]
      .getTotal(CheckupExpenseFilters.Empty)
      .map { total =>
        assert(total >= 1)
      }
      .handleError { error =>
        println("ERROR::::::::::::::::::: " + error)
        failure("Test failed.")
      }
  }

  test("Get All Doctor Shares") { implicit postgres =>
    CheckupExpensesRepository
      .make[F]
      .getDoctorShares
      .map { doctorShares =>
        assert(doctorShares.nonEmpty)
      }
      .handleError { error =>
        println("ERROR::::::::::::::::::: " + error)
        failure("Test failed.")
      }
  }

  test("Create Checkup Expense") { implicit postgres =>
    CheckupExpensesRepository
      .make[F]
      .create(data.doctorShare.data2.serviceId)
      .map { checkupExpense =>
        assert(checkupExpense.doctorShareId == data.doctorShare.id2)
      }
      .handleError {
        fail("Test failed.")
      }
  }
}
