package babymed.services.users.repositories

import babymed.services.users.generators.UserGenerators
import babymed.test.DBSuite
import cats.effect.IO
import weaver.Expectations

import java.time.LocalDateTime

object UserRepositorySpec extends DBSuite with UserGenerators {
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
