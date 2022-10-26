package babymed.services.users.boundary

import babymed.refinements.Phone
import babymed.services.users.domain.UserAndHash
import babymed.services.users.proto
import babymed.services.users.repositories.UsersRepository

class Users[F[_]](usersRepository: UsersRepository[F]) extends proto.Users[F]{
  override def find(phone: Phone): F[Option[UserAndHash]] =
    usersRepository.findByPhone(phone)
}
