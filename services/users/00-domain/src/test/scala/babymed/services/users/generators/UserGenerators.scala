package babymed.services.users.generators

import org.scalacheck.Gen
import tsec.passwordhashers.jca.SCrypt

import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.EditUser
import babymed.services.users.domain.SubRole
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.domain.types.SubRoleId

trait UserGenerators extends TypeGen {
  val userGen: Gen[User] =
    for {
      id <- userIdGen
      createdAt <- localDateTimeGen
      firstname <- firstNameGen
      lastname <- lastNameGen
      role <- roleGen
      phone <- phoneGen
      subRoleId <- subRoleIdGen.opt
    } yield User(id, createdAt, firstname, lastname, phone, role, subRoleId)

  def createUserGen(
      maybeSubRoleId: Option[SubRoleId] = None
    ): Gen[CreateUser] =
    for {
      firstname <- firstNameGen
      lastname <- lastNameGen
      role <- roleGen
      phone <- phoneGen
      subRoleId <- subRoleIdGen.opt
    } yield CreateUser(firstname, lastname, phone, role, maybeSubRoleId.orElse(subRoleId))

  val userAndHashGen: Gen[UserAndHash] =
    for {
      user <- userGen
      password <- passwordGen
    } yield UserAndHash(user, SCrypt.hashpwUnsafe(password.value))

  def editUserGen(
      maybeSubRoleId: Option[SubRoleId] = None
    ): Gen[EditUser] =
    for {
      id <- userIdGen
      firstname <- firstNameGen
      lastname <- lastNameGen
      phone <- phoneGen
      role <- roleGen
      subRoleId <- subRoleIdGen.opt
    } yield EditUser(id, firstname, lastname, phone, role, maybeSubRoleId.orElse(subRoleId))

  val subRoleGen: Gen[SubRole] =
    for {
      id <- subRoleIdGen
      name <- subRoleNameGen
    } yield SubRole(id, name)
}
