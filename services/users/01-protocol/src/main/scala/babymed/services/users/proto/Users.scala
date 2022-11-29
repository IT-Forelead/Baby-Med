package babymed.services.users.proto

import io.circe.refined._

import babymed.domain.ResponseData
import babymed.refinements.Phone
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.EditUser
import babymed.services.users.domain.SubRole
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.domain.UserFilters
import babymed.services.users.domain.types.UserId
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec

@service(Custom)
trait Users[F[_]] {
  def validationAndCreate(createUser: CreateUser): F[User]
  def validationAndEdit(editUser: EditUser): F[Unit]
  def find(phone: Phone): F[Option[UserAndHash]]
  def get(filters: UserFilters): F[ResponseData[User]]
  def getSubRoles: F[List[SubRole]]
  def delete(userId: UserId): F[Unit]
  def getTotal(filters: UserFilters): F[Long]
}

object Users {}
