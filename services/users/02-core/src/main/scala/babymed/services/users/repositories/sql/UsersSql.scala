package babymed.services.users.repositories.sql

import java.time.LocalDateTime

import skunk._
import skunk.codec.all._
import skunk.implicits._
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

import babymed.refinements.Phone
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.EditUser
import babymed.services.users.domain.SubRole
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.domain.UserFilters
import babymed.services.users.domain.types.UserId
import babymed.support.skunk.codecs.phone
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps

object UsersSql {
  private val Columns =
    userId ~ timestamp ~ firstName ~ lastName ~ phone ~ role ~ subRoleId.opt ~ passwordHash ~ bool

  val encoder: Encoder[UserId ~ LocalDateTime ~ CreateUser ~ PasswordHash[SCrypt]] =
    Columns.contramap {
      case id ~ createdAt ~ cu ~ password =>
        id ~ createdAt ~ cu.firstname ~ cu.lastname ~ cu.phone ~ cu.role ~ cu.subRoleId ~ password ~ false
    }

  val decoderUserAndHash: Decoder[UserAndHash] = Columns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ phone ~ role ~ subRoleId ~ password ~ _ =>
      UserAndHash(
        user = User(id, createdAt, firstName, lastName, phone, role, subRoleId),
        password = password,
      )
  }

  val decoder: Decoder[User] = Columns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ phone ~ role ~ subRoleId ~ _ ~ _ =>
      User(id, createdAt, firstName, lastName, phone, role, subRoleId)
  }

  val decUserWithSubRole: Decoder[User] = (Columns ~ subRoleName.opt).map {
    case id ~ createdAt ~ firstName ~ lastName ~ phone ~ role ~ subRoleId ~ _ ~ _ ~ subRoleName =>
      User(id, createdAt, firstName, lastName, phone, role, subRoleId, subRoleName)
  }

  val decSubRole: Decoder[SubRole] = (subRoleId ~ subRoleName ~ bool).map {
    case id ~ name ~ _ =>
      SubRole(id, name)
  }

  private def userFilters(filters: UserFilters): List[Option[AppliedFragment]] =
    List(
      filters.firstName.map(sql"users.firstname ILIKE $firstName"),
      filters.lastName.map(sql"users.lastname ILIKE $lastName"),
      filters.role.map(sql"users.role = $role"),
      filters.phone.map(sql"users.phone ILIKE $phone ORDER BY role ASC"),
    )

  def select(filters: UserFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT users.*, sub_roles.name FROM users
        LEFT JOIN sub_roles ON users.sub_role_id = sub_roles.id
        WHERE users.deleted = false"""
    baseQuery(Void).andOpt(userFilters(filters): _*)
  }

  def total(filters: UserFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] = sql"""SELECT count(*) FROM users WHERE deleted = false"""
    baseQuery(Void).andOpt(userFilters(filters): _*)
  }

  val insert: Query[UserId ~ LocalDateTime ~ CreateUser ~ PasswordHash[SCrypt], User] =
    sql"""INSERT INTO users VALUES ($encoder) RETURNING *""".query(decoder)

  val selectByPhone: Query[Phone, UserAndHash] =
    sql"""SELECT * FROM users WHERE phone = $phone AND deleted = false""".query(decoderUserAndHash)

  val selectOldUser: Query[Phone, UserId] =
    sql"""SELECT id FROM users WHERE phone = $phone""".query(userId)

  val updateUserSql: Command[EditUser] =
    sql"""UPDATE users SET firstname = $firstName, lastname = $lastName, phone = $phone, role = $role, sub_role_id = ${subRoleId.opt}
        WHERE id = $userId"""
      .command
      .contramap { eu: EditUser =>
        eu.firstname ~ eu.lastname ~ eu.phone ~ eu.role ~ eu.subRoleId ~ eu.id
      }

  val deleteUserSql: Command[UserId] =
    sql"""UPDATE users SET deleted = true WHERE id = $userId""".command

  val selectSubRoles: Query[Void, SubRole] =
    sql"""SELECT * FROM sub_roles WHERE deleted = false""".query(decSubRole)
}
