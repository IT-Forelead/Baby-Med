package babymed.services.users.boundary

import cats.effect.kernel.Sync
import org.scalacheck.Gen

import babymed.refinements.Password
import babymed.refinements.Phone
import babymed.services.messages.domain.CreateMessage
import babymed.services.messages.domain.Message
import babymed.services.messages.proto.Messages
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.EditUser
import babymed.services.users.domain.SubRole
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.domain.UserFilters
import babymed.services.users.domain.types.UserId
import babymed.services.users.generators.UserGenerators
import babymed.services.users.repositories.UsersRepository
import babymed.test.TestSuite

object UsersSpec extends TestSuite with UserGenerators {
  val userRepo: UsersRepository[F] = new UsersRepository[F] {
    override def validationAndCreate(
        createUser: CreateUser,
        sendSms: Password => F[Unit],
      ): F[User] =
      Sync[F].delay(userGen.get)

    override def validationAndEdit(editUser: EditUser): F[Unit] =
      Sync[F].unit
    override def findByPhone(phone: Phone): F[Option[UserAndHash]] =
      Sync[F].delay(userAndHashGen.getOpt)
    override def get(filters: UserFilters): F[List[User]] =
      Sync[F].delay(List(userGen.get))
    override def delete(userId: UserId): UsersSpec.F[Unit] =
      Sync[F].unit
    override def getTotal(filters: UserFilters): UsersSpec.F[Long] =
      Sync[F].delay(Gen.long.get)
    override def getSubRoles: F[List[SubRole]] =
      Sync[F].delay(List(subRoleGen.get))
  }

  val messageRepo: Messages[F] = new Messages[F] {
    override def send(createMessage: CreateMessage): F[Message] = ???
  }

  val users: Users[F] = new Users[F](userRepo, messageRepo)

  loggedTest("Create User") { logger =>
    users
      .validationAndCreate(createUserGen().get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Edit User") { logger =>
    users
      .validationAndEdit(editUserGen().get)
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

  loggedTest("Get Users") { implicit logger =>
    users
      .get(UserFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Delete User") { implicit logger =>
    users
      .delete(userId = userIdGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get All Sub Roles") { implicit logger =>
    users
      .getSubRoles
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }
}
