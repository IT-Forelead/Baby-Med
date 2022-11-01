package babymed.services.users.repositories

import java.time.LocalDateTime

import babymed.domain.ID
import babymed.refinements.Phone
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.User
import babymed.services.users.domain.UserAndHash
import babymed.services.users.domain.types.UserId
import babymed.support.skunk.syntax.all.skunkSyntaxQueryOps
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.util.RandomGenerator
import cats.effect.MonadCancel
import cats.effect.Resource
import cats.effect.Sync
import cats.implicits._
import skunk._
import skunk.implicits.toIdOps
import tsec.passwordhashers.jca.SCrypt

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
