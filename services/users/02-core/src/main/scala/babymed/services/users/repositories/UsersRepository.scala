package babymed.services.users.repositories

import babymed.refinements.Phone
import babymed.services.users.domain.UserAndHash
import babymed.support.skunk.syntax.all.skunkSyntaxQueryOps
import cats.effect.MonadCancel
import cats.effect.Resource
import skunk.Session

trait UsersRepository[F[_]] {
  def findByPhone(phone: Phone): F[Option[UserAndHash]]
}

object UsersRepository {
  def make[F[_]](
      implicit
      session: Resource[F, Session[F]],
      F: MonadCancel[F, Throwable],
    ): UsersRepository[F] = new UsersRepository[F] {
    import sql.UsersSql._

    override def findByPhone(phone: Phone): F[Option[UserAndHash]] =
      selectByPhone.queryOption(phone)

  }
}
