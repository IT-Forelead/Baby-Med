package babymed.services.users.boundary

import babymed.Phone
import babymed.services.users.domain.{CreateUser, User, UserAndHash}
import babymed.services.users.proto
import babymed.services.users.repositories.UsersRepository

class Users[F[_]](usersRepository: UsersRepository[F]) extends proto.Users[F]{
  override def create(createUser: CreateUser): F[User] =
    usersRepository.create(createUser)
  override def find(phone: Phone): F[Option[UserAndHash]] =
    usersRepository.findByPhone(phone)
}
