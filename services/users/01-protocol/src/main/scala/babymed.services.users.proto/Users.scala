package babymed.services.users.proto

import babymed.Phone
import babymed.services.users.domain.UserAndHash
import babymed.support.services.service
import higherkindness.mu.rpc.protocol.Custom

@service(Custom)
trait Users[F[_]] {
  def find(phone: Phone): F[Option[UserAndHash]]
}

object Users {}
