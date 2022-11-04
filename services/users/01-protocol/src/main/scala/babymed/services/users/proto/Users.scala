package babymed.services.users.proto

import io.circe.refined._

import babymed.refinements.Phone
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.domain.UserFilters
import babymed.services.users.domain.types.UserId
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec

@service(Custom)
trait Users[F[_]] {
  def validationAndCreate(createUser: CreateUser): F[User]
  def find(phone: Phone): F[Option[UserAndHash]]
  def get(filters: UserFilters): F[List[User]]
  def delete(userId: UserId): F[Unit]
}

object Users {}
