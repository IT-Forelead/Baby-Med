package babymed.services.users.generators

import org.scalacheck.Gen
import tsec.passwordhashers.jca.SCrypt

import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash

trait UserGenerators extends TypeGen {
  def userGen: Gen[User] =
    User(
      id = userIdGen.get,
      createdAt = localDateTimeGen.get,
      firstname = firstNameGen.get,
      lastname = lastNameGen.get,
      role = roleGen.get,
      phone = phoneGen.get,
    )

  def createUserGen: Gen[CreateUser] =
    CreateUser(
      firstname = firstNameGen.get,
      lastname = lastNameGen.get,
      role = roleGen.get,
      phone = phoneGen.get,
    )

  def userAndHashGen: Gen[UserAndHash] =
    UserAndHash(
      user = userGen.get,
      password = SCrypt.hashpwUnsafe(passwordGen.get.value),
    )
}
