package babymed.services.users.repositories.sql

import java.time.LocalDateTime

import skunk._
import skunk.codec.all.timestamp
import skunk.implicits._
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

import babymed.refinements.Phone
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.EditUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.domain.UserFilters
import babymed.services.users.domain.types.UserId
import babymed.support.skunk.codecs.phone
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps

object UsersSql {
  val userId: Codec[UserId] = identity[UserId]

  private val Columns =
    userId ~ timestamp ~ firstName ~ lastName ~ phone ~ role ~ subRoleId.opt ~ passwordHash

  val encoder: Encoder[UserId ~ LocalDateTime ~ CreateUser ~ PasswordHash[SCrypt]] =
    Columns.contramap {
      case id ~ createdAt ~ cu ~ password =>
        id ~ createdAt ~ cu.firstname ~ cu.lastname ~ cu.phone ~ cu.role ~ cu.subRoleId ~ password
    }

  val decoderUserAndHash: Decoder[UserAndHash] = Columns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ phone ~ role ~ subRoleId ~ password =>
      UserAndHash(
        user = User(id, createdAt, firstName, lastName, phone, role, subRoleId),
        password = password,
      )
  }

  val decoder: Decoder[User] = Columns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ phone ~ role ~ subRoleId ~ _ =>
      User(id, createdAt, firstName, lastName, phone, role, subRoleId)
  }

  private def userFilters(filters: UserFilters): List[Option[AppliedFragment]] =
    List(
      filters.firstName.map(sql"firstname ILIKE $firstName"),
      filters.lastName.map(sql"lastname ILIKE $lastName"),
      filters.role.map(sql"role = $role"),
      filters.phone.map(sql"phone ILIKE $phone"),
    )

  def select(filters: UserFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] = sql"""SELECT * FROM users"""
    baseQuery(Void).whereAndOpt(userFilters(filters): _*)
  }

  def total(filters: UserFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] = sql"""SELECT count(*) FROM users"""
    baseQuery(Void).whereAndOpt(userFilters(filters): _*)
  }

  val insert: Query[UserId ~ LocalDateTime ~ CreateUser ~ PasswordHash[SCrypt], User] =
    sql"""INSERT INTO users VALUES ($encoder) RETURNING *""".query(decoder)

  val selectByPhone: Query[Phone, UserAndHash] =
    sql"""SELECT * FROM users WHERE phone = $phone""".query(decoderUserAndHash)

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
    sql"""DELETE FROM users WHERE id = $userId""".command
}
