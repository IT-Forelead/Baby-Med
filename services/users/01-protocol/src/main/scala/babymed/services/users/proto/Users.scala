package babymed.services.users.proto

import babymed.refinements.Phone
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec
import io.circe.refined._

@service(Custom)
trait Users[F[_]] {
  def create(createUser: CreateUser): F[User]
  def find(phone: Phone): F[Option[UserAndHash]]
}

object Users {}
