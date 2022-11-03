package babymed.services.users.proto

import io.circe.refined._

import babymed.refinements.Phone
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec

@service(Custom)
trait Users[F[_]] {
  def validationAndCreate(createUser: CreateUser): F[User]
  def find(phone: Phone): F[Option[UserAndHash]]
}

object Users {}
