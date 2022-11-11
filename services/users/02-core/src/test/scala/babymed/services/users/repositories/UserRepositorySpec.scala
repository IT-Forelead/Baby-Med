package babymed.services.users.repositories

import java.time.LocalDateTime

import cats.effect._
import cats.implicits._
import skunk.Session
import weaver.Expectations

import babymed.services.users.domain.UserFilters
import babymed.services.users.generators.UserGenerators
import babymed.support.database.DBSuite

object UserRepositorySpec extends DBSuite with UserGenerators {
  override def schemaName: String = "public"
  override def beforeAll(implicit res: Res): IO[Unit] = data.setup
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
    object Case1 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(UserFilters(firstName = data.user.data1.firstname.some))
          .map { users =>
            assert(users.map(_.id).contains(data.user.id1))
          }
    }
    object Case2 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(UserFilters(lastName = data.user.data2.lastname.some))
          .map { users =>
            assert(users.map(_.id).contains(data.user.id2))
          }
    }
    object Case3 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(UserFilters(role = data.user.data3.role.some))
          .map { users =>
            assert.all(
              users.map(_.id).contains(data.user.id3),
              users.forall(_.role == data.user.data3.role),
            )
          }
    }
    object Case4 extends TestCase[Res] {
      override def check(implicit dao: Resource[IO, Session[IO]]): IO[Expectations] =
        repo
          .get(UserFilters(phone = data.user.data1.phone.some))
          .map { users =>
            assert.same(users.map(_.id), List(data.user.id1))
          }
    }
    List(
      Case1,
      Case2,
      Case3,
      Case4,
    ).traverse(_.check).map(_.reduce(_ and _))
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
