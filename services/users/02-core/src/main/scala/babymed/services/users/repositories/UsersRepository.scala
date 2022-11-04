package babymed.services.users.repositories

import cats.data.OptionT
import cats.effect.Async
import cats.effect.MonadCancel
import cats.effect.Resource
import cats.implicits._
import skunk._
import skunk.implicits.toIdOps
import tsec.passwordhashers.jca.SCrypt

import babymed.domain.ID
import babymed.effects.Calendar
import babymed.exception.PhoneInUse
import babymed.refinements.Phone
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.domain.UserFilters
import babymed.services.users.domain.types.UserId
import babymed.services.users.repositories.sql.UsersSql
import babymed.support.skunk.syntax.all._
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.util.RandomGenerator

trait UsersRepository[F[_]] {
  def validationAndCreate(createUser: CreateUser): F[User]
  def findByPhone(phone: Phone): F[Option[UserAndHash]]
  def get(filters: UserFilters): F[List[User]]
  def delete(userId: UserId): F[Unit]
}

object UsersRepository {
  def make[F[_]: Async](
      implicit
      session: Resource[F, Session[F]],
      F: MonadCancel[F, Throwable],
    ): UsersRepository[F] = new UsersRepository[F] {
    import sql.UsersSql._

    private def create(createUser: CreateUser): F[User] =
      for {
        id <- ID.make[F, UserId]
        now <- Calendar[F].currentDateTime
        password <- SCrypt.hashpw[F](RandomGenerator.randomPassword(6))
        user <- insert.queryUnique(id ~ now ~ createUser ~ password)
      } yield user

    private def updateOldUser(id: UserId, createUser: CreateUser): F[User] =
      for {
        now <- Calendar[F].currentDateTime
        password <- SCrypt.hashpw[F](RandomGenerator.randomPassword(6))
        user <- updateOldUserSql.queryUnique(id ~ now ~ createUser ~ password)
      } yield user

    override def validationAndCreate(createUser: CreateUser): F[User] =
      OptionT(selectOldUser.queryOption(createUser.phone))
        .semiflatMap(oldUser =>
          if (oldUser.deleted)
            updateOldUser(oldUser.id, createUser = createUser)
          else
            PhoneInUse(createUser.phone).raiseError[F, User]
        )
        .getOrElseF(create(createUser))

    override def findByPhone(phone: Phone): F[Option[UserAndHash]] =
      selectByPhone.queryOption(phone)

    override def get(filters: UserFilters): F[List[User]] = {
      val query = UsersSql.select(filters).paginateOpt(filters.limit, filters.page)
      query.fragment.query(UsersSql.decoder).queryList(query.argument)
    }

    override def delete(userId: UserId): F[Unit] =
      updateDeletedStatus.execute(userId)
  }
}
