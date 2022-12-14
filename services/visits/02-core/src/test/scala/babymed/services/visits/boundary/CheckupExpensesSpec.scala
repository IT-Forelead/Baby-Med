package babymed.services.visits.boundary

import cats.effect.kernel.Sync
import org.scalacheck.Gen

import babymed.services.visits.domain._
import babymed.services.visits.generators.CheckupExpenseGenerators
import babymed.services.visits.repositories.CheckupExpensesRepository
import babymed.test.TestSuite

object CheckupExpensesSpec extends TestSuite with CheckupExpenseGenerators {
  val checkupExpenseRepo: CheckupExpensesRepository[F] = new CheckupExpensesRepository[F] {
    override def create(
        createCheckupExpenses: List[CreateCheckupExpense]
      ): F[List[CheckupExpense]] =
      Sync[F].delay(List(checkupExpenseGen.get))
    override def createDoctorShare(createData: CreateDoctorShare): F[DoctorShare] =
      Sync[F].delay(doctorShareGen.get)
    override def get(filters: CheckupExpenseFilters): F[List[CheckupExpenseInfo]] =
      Sync[F].delay(List(checkupExpenseInfoGen.get))
    override def getTotal(filters: CheckupExpenseFilters): F[Long] =
      Sync[F].delay(Gen.long.get)
    override def getDoctorShares: F[List[DoctorShareInfo]] =
      Sync[F].delay(List(doctorShareInfoGen.get))
    override def deleteDoctorShare(id: types.DoctorShareId): F[Unit] =
      Sync[F].unit
  }

  val checkupExpenses: CheckupExpenses[F] = new CheckupExpenses[F](checkupExpenseRepo)

  loggedTest("Create Checkup Expense") { logger =>
    checkupExpenses
      .create(List(createCheckupExpenseGen().get))
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Checkup Expenses") { logger =>
    checkupExpenses
      .get(CheckupExpenseFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get Checkup Expenses Total") { logger =>
    checkupExpenses
      .getTotal(CheckupExpenseFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Create Doctor Share") { logger =>
    checkupExpenses
      .createDoctorShare(createDoctorShareGen().get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get All Doctor Shares") { logger =>
    checkupExpenses
      .getDoctorShares
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Delete Doctor Share") { logger =>
    checkupExpenses
      .deleteDoctorShare(doctorShareIdGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }
}
