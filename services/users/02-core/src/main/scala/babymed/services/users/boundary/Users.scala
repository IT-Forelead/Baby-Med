package babymed.services.users.boundary

import babymed.refinements.Phone
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.EditUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.domain.UserFilters
import babymed.services.users.domain.types.UserId
import babymed.services.users.proto
import babymed.services.users.repositories.UsersRepository

class Users[F[_]](usersRepository: UsersRepository[F]) extends proto.Users[F] {
  override def validationAndCreate(createUser: CreateUser): F[User] =
    usersRepository.validationAndCreate(createUser)
  override def validationAndEdit(editUser: EditUser): F[Unit] =
    usersRepository.validationAndEdit(editUser)
  override def find(phone: Phone): F[Option[UserAndHash]] =
    usersRepository.findByPhone(phone)
  override def get(filters: UserFilters): F[List[User]] =
    usersRepository.get(filters)
  override def delete(userId: UserId): F[Unit] =
    usersRepository.delete(userId)
}
