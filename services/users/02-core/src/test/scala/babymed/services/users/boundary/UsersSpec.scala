package babymed.services.users.boundary

import babymed.refinements.Phone
import babymed.services.users.domain.{CreateUser, User, UserAndHash}
import babymed.services.users.generators.UserGenerators
import babymed.services.users.repositories.UsersRepository
import babymed.test.TestSuite
import cats.effect.kernel.Sync

object UsersSpec extends TestSuite with UserGenerators {
  val userRepo: UsersRepository[F] = new UsersRepository[F] {
    override def create(createUser: CreateUser): UsersSpec.F[User] =
      Sync[F].delay(userGen.get)
    override def findByPhone(phone: Phone): F[Option[UserAndHash]] =
      Sync[F].delay(userAndHashGen.getOpt)
  }

  val users: Users[F] = new Users[F](userRepo)

  loggedTest("Create User") { logger =>
    users
      .create(createUserGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Find User By Phone") { implicit logger =>
    users
      .find(phone = phoneGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

}
