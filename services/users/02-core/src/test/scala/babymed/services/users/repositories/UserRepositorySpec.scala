package babymed.services.users.repositories

import java.time.LocalDateTime

import cats.effect.IO

import babymed.services.users.generators.UserGenerators
import babymed.support.database.DBSuite

object UserRepositorySpec extends DBSuite with UserGenerators {
  override def schemaName: String = "public"

  test("Create User") { implicit postgres =>
    UsersRepository
      .make[F]
      .create(createUserGen.get)
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
    repo.create(createUser) >>
      repo
        .findByPhone(createUser.phone)
        .map { optUser =>
          assert(optUser.map(_.user.phone).contains(createUser.phone))
        }
        .handleError(fail("Should return product exchange"))
  }
}
