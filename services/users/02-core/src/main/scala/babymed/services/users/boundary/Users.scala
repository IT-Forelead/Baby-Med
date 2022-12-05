package babymed.services.users.boundary

import cats.Monad
import cats.implicits._

import babymed.domain.MessageType.Registration
import babymed.domain.ResponseData
import babymed.integrations.opersms.domain.DeliveryStatus.SENT
import babymed.refinements.Password
import babymed.refinements.Phone
import babymed.services.messages.domain.CreateMessage
import babymed.services.messages.domain.types.MessageText
import babymed.services.messages.proto.Messages
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.EditUser
import babymed.services.users.domain.SubRole
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.domain.UserFilters
import babymed.services.users.domain.types.UserId
import babymed.services.users.proto
import babymed.services.users.repositories.UsersRepository
import babymed.syntax.refined.commonSyntaxAutoRefineV

class Users[F[_]: Monad](usersRepository: UsersRepository[F], messages: Messages[F])
    extends proto.Users[F] {
  private def sendPassword(phone: Phone)(password: Password): F[Unit] = {
    val createMessage = CreateMessage(
      phone = phone,
      text = MessageText(s"Siz tizimdan ro'yhatdan o'tdinmgiz. Parolingiz: $password"),
      messageType = Registration,
      deliveryStatus = SENT,
    )
    messages.send(createMessage).void
  }

  override def validationAndCreate(createUser: CreateUser): F[User] =
    usersRepository.validationAndCreate(createUser, sendPassword(createUser.phone))
  override def validationAndEdit(editUser: EditUser): F[Unit] =
    usersRepository.validationAndEdit(editUser)
  override def find(phone: Phone): F[Option[UserAndHash]] =
    usersRepository.findByPhone(phone)
  override def get(filters: UserFilters): F[ResponseData[User]] =
    for {
      users <- usersRepository.get(filters)
      total <- usersRepository.getTotal(filters)
    } yield ResponseData(users, total)
  override def delete(userId: UserId): F[Unit] =
    usersRepository.delete(userId)
  override def getTotal(filters: UserFilters): F[Long] =
    usersRepository.getTotal(filters)
  override def getSubRoles: F[List[SubRole]] =
    usersRepository.getSubRoles
}
