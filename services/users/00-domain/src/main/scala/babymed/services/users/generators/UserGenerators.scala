package babymed.services.users.generators

import babymed.services.users.domain.{User, UserAndHash}
import org.scalacheck.Gen
import tsec.passwordhashers.jca.SCrypt

trait UserGenerators extends TypeGen {
  def userGen: Gen[User] =
    User(
      id = userIdGen.get,
      firstname = firstNameGen.get,
      lastname = lastNameGen.get,
      role = roleGen.get,
      phone = phoneGen.get
    )

  def userAndHashGen: Gen[UserAndHash] =
    UserAndHash(
      user = userGen.get,
      password = SCrypt.hashpwUnsafe(passwordGen.get.value)
    )
}
