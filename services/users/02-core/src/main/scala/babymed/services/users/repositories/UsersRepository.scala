package babymed.services.users.repositories

import cats.implicits._
import babymed.refinements.Phone
import babymed.domain.ID
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.services.users.domain.types.UserId
import babymed.services.users.domain.{CreateUser, User, UserAndHash}
import babymed.support.skunk.syntax.all.skunkSyntaxQueryOps
import babymed.util.RandomGenerator
import cats.effect.{MonadCancel, Resource, Sync}
import skunk._
import skunk.implicits.toIdOps
import tsec.passwordhashers.jca.SCrypt

import java.time.LocalDateTime

trait UsersRepository[F[_]] {
  def create(createUser: CreateUser): F[User]
  def findByPhone(phone: Phone): F[Option[UserAndHash]]
}

object UsersRepository {
  def make[F[_]: Sync](
      implicit
      session: Resource[F, Session[F]],
      F: MonadCancel[F, Throwable],
    ): UsersRepository[F] = new UsersRepository[F] {
    import sql.UsersSql._

    override def create(createUser: CreateUser): F[User] =
      for {
        id <- ID.make[F, UserId]
        now <- Sync[F].delay(LocalDateTime.now())
        password <- SCrypt.hashpw[F](RandomGenerator.randomPassword(6))
        user <- insert.queryUnique(id ~ now ~ createUser ~ password)
      } yield user

    override def findByPhone(phone: Phone): F[Option[UserAndHash]] =
      selectByPhone.queryOption(phone)

  }
}
