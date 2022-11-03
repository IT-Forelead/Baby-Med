package babymed.services.users.repositories.sql

import java.time.LocalDateTime

import skunk._
import skunk.codec.all.bool
import skunk.codec.all.timestamp
import skunk.implicits._
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

import babymed.refinements.Phone
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.OldUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.domain.types.UserId
import babymed.support.skunk.codecs.phone

object UsersSql {
  val userId: Codec[UserId] = identity[UserId]

  private val ColumnsWithPassword =
    userId ~ timestamp ~ firstName ~ lastName ~ phone ~ role ~ passwordHash
  private val Columns = userId ~ timestamp ~ firstName ~ lastName ~ phone ~ role

  val encoder: Encoder[UserId ~ LocalDateTime ~ CreateUser ~ PasswordHash[SCrypt]] =
    ColumnsWithPassword.contramap {
      case id ~ createdAt ~ cu ~ password =>
        id ~ createdAt ~ cu.firstname ~ cu.lastname ~ cu.phone ~ cu.role ~ password
    }

  val decoderUserAndHash: Decoder[UserAndHash] = ColumnsWithPassword.map {
    case id ~ createdAt ~ firstName ~ lastName ~ phone ~ role ~ password =>
      UserAndHash(
        user = User(id, createdAt, firstName, lastName, phone, role),
        password = password,
      )
  }

  val decoder: Decoder[User] = Columns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ phone ~ role =>
      User(id, createdAt, firstName, lastName, phone, role)
  }

  val decOldUser: Decoder[OldUser] = (Columns ~ bool).map {
    case id ~ createdAt ~ firstName ~ lastName ~ phone ~ role ~ deleted =>
      OldUser(id, createdAt, firstName, lastName, phone, role, deleted)
  }

  val insert: Query[UserId ~ LocalDateTime ~ CreateUser ~ PasswordHash[SCrypt], User] =
    sql"""INSERT INTO users VALUES ($encoder) RETURNING id, created_at, firstname, lastname, phone, role"""
      .query(decoder)

  val selectByPhone: Query[Phone, UserAndHash] =
    sql"""SELECT id, created_at, firstname, lastname, phone, role, password FROM users
         WHERE phone = $phone AND deleted = false"""
      .query(decoderUserAndHash)

  val selectOldUser: Query[Phone, OldUser] =
    sql"""SELECT id, created_at, firstname, lastname, phone, role, deleted FROM users
         WHERE phone = $phone"""
      .query(decOldUser)

  val updateOldUserSql: Query[UserId ~ LocalDateTime ~ CreateUser ~ PasswordHash[SCrypt], User] =
    sql"""UPDATE users SET created_at = $timestamp,
          firstname = $firstName,
          lastname = $lastName,
          phone = $phone,
          role = $role,
          password = $passwordHash,
          deleted = false
    WHERE id = $userId
    RETURNING id, created_at, firstname, lastname, phone, role"""
      .query(decoder)
      .contramap[UserId ~ LocalDateTime ~ CreateUser ~ PasswordHash[SCrypt]] {
        case id ~ createdAt ~ cu ~ password =>
          createdAt ~ cu.firstname ~ cu.lastname ~ cu.phone ~ cu.role ~ password ~ id
      }
}
