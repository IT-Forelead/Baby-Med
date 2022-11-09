package babymed.services.users.generators

import org.scalacheck.Gen
import tsec.passwordhashers.jca.SCrypt

import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.EditUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash

trait UserGenerators extends TypeGen {
  val userGen: Gen[User] =
    for {
      id <- userIdGen
      createdAt <- localDateTimeGen
      firstname <- firstNameGen
      lastname <- lastNameGen
      role <- roleGen
      phone <- phoneGen
    } yield User(id, createdAt, firstname, lastname, phone, role)

  val createUserGen: Gen[CreateUser] =
    for {
      firstname <- firstNameGen
      lastname <- lastNameGen
      role <- roleGen
      phone <- phoneGen
    } yield CreateUser(firstname, lastname, phone, role)

  val userAndHashGen: Gen[UserAndHash] =
    for {
      user <- userGen
      password <- passwordGen
    } yield UserAndHash(user, SCrypt.hashpwUnsafe(password.value))

  val editUserGen: Gen[EditUser] =
    for {
      id <- userIdGen
      firstname <- firstNameGen
      lastname <- lastNameGen
      phone <- phoneGen
      role <- roleGen
    } yield EditUser(id, firstname, lastname, phone, role)
}
