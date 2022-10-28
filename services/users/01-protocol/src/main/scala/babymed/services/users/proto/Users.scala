package babymed.services.users.proto

import babymed.Phone
import babymed.services.users.domain.{CreateUser, User, UserAndHash}
import babymed.support.services.service
import higherkindness.mu.rpc.protocol.Custom
import babymed.support.services.syntax.marshaller.codec
import io.circe.refined._

@service(Custom)
trait Users[F[_]] {
  def create(createUser: CreateUser): F[User]
  def find(phone: Phone): F[Option[UserAndHash]]
}

object Users {}