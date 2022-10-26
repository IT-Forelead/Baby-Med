package babymed.services.users.proto

import babymed.Phone
import babymed.services.users.domain.UserAndHash
import babymed.support.services.service
import higherkindness.mu.rpc.protocol.Custom
import babymed.support.services.syntax.marshaller.codec
import io.circe.refined._
import eu.timepit.refined.cats._

@service(Custom)
trait Users[F[_]] {
  def find(phone: Phone): F[Option[UserAndHash]]
}

object Users {}
