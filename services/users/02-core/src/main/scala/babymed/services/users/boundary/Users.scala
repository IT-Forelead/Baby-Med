package babymed.services.users.boundary

import cats.Monad
import cats.implicits._

import babymed.refinements.Phone
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.EditUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.domain.UserFilters
import babymed.services.users.domain.UsersWithTotal
import babymed.services.users.domain.types.UserId
import babymed.services.users.proto
import babymed.services.users.repositories.UsersRepository

class Users[F[_]: Monad](usersRepository: UsersRepository[F]) extends proto.Users[F] {
  override def validationAndCreate(createUser: CreateUser): F[User] =
    usersRepository.validationAndCreate(createUser)
  override def validationAndEdit(editUser: EditUser): F[Unit] =
    usersRepository.validationAndEdit(editUser)
  override def find(phone: Phone): F[Option[UserAndHash]] =
    usersRepository.findByPhone(phone)
  override def get(filters: UserFilters): F[UsersWithTotal] =
    for {
      users <- usersRepository.get(filters)
      total <- usersRepository.getTotal(filters)
    } yield UsersWithTotal(users, total)
  override def delete(userId: UserId): F[Unit] =
    usersRepository.delete(userId)
  override def getTotal(filters: UserFilters): F[Long] =
    usersRepository.getTotal(filters)
}
