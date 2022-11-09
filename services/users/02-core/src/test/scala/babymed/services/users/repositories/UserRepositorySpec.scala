package babymed.services.users.repositories

import java.time.LocalDateTime

import cats.effect.IO

import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.UserFilters
import babymed.services.users.generators.UserGenerators
import babymed.support.database.DBSuite

object UserRepositorySpec extends DBSuite with UserGenerators {
  override def schemaName: String = "public"

  test("Create User") { implicit postgres =>
    UsersRepository
      .make[F]
      .validationAndCreate(createUserGen.get)
      .map { pr =>
        assert(pr.createdAt.isBefore(LocalDateTime.now()))
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Find User by Phone") { implicit postgres =>
    val repo = UsersRepository.make[IO]
    val createUser = createUserGen.get
    repo.validationAndCreate(createUser) >>
      repo
        .findByPhone(createUser.phone)
        .map { optUser =>
          assert(optUser.map(_.user.phone).contains(createUser.phone))
        }
        .handleError(fail("Should return product exchange"))
  }

  test("Get Users") { implicit postgres =>
    val repo = UsersRepository.make[F]
    val createUser: CreateUser = createUserGen.get

    repo.validationAndCreate(createUser) *>
      repo
        .get(UserFilters.Empty)
        .map { users =>
          assert(users.exists(_.phone == createUser.phone))
        }
        .handleError { error =>
          println("ERROR::::::::::::::::::: " + error)
          failure("Test failed.")
        }
  }

  test("Get Empty Users List With Filter") { implicit postgres =>
    val repo = UsersRepository.make[F]
    val createUser: CreateUser = createUserGen.get

    repo.validationAndCreate(createUser) *>
      repo
        .get(userFiltersGen.get)
        .map { users =>
          assert(users.isEmpty)
        }
        .handleError { error =>
          println("ERROR::::::::::::::::::: " + error)
          failure("Test failed.")
        }
  }

  test("Delete User") { implicit postgres =>
    val repo = UsersRepository.make[IO]
    val create = createUserGen.get
    for {
      createUser <- repo.validationAndCreate(create)
      _ <- repo.delete(createUser.id)
      users <- repo.get(UserFilters.Empty)
    } yield assert(users.exists(_.id != createUser.id))
  }

  test("Edit User") { implicit postgres =>
    val repo = UsersRepository.make[IO]
    val create = createUserGen.get
    val editUser = editUserGen.get
    for {
      createUser <- repo.validationAndCreate(create)
      _ <- repo.validationAndEdit(editUser.copy(id = createUser.id))
      users <- repo.get(UserFilters.Empty)
    } yield assert(users.exists(_.phone == editUser.phone))
  }
}
