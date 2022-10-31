package babymed.services.payments.repositories

import babymed.domain.ID
import babymed.effects.{Calendar, GenUUID}
import babymed.services.payments.domain.types.PaymentId
import babymed.services.payments.domain.{CreatePayment, Payment, SearchFilters}
import babymed.services.payments.repositories.sql.PaymentsSql
import babymed.support.skunk.syntax.all._
import cats.effect.{Concurrent, MonadCancel, Resource}
import cats.implicits._
import skunk._
import skunk.implicits._

trait PaymentsRepository[F[_]] {
  def create(createPayment: CreatePayment): F[Payment]
  def get(searchFilters: SearchFilters): F[List[Payment]]
}

object PaymentsRepository {
  def make[F[_]: GenUUID: Calendar: Concurrent](
    implicit
    session: Resource[F, Session[F]],
    F: MonadCancel[F, Throwable],
  ): PaymentsRepository[F] = new PaymentsRepository[F] {

    override def create(createPayment: CreatePayment): F[Payment] =
      for {
        id <- ID.make[F, PaymentId]
        now <- Calendar[F].currentDateTime
        payment <- PaymentsSql.insert.queryUnique(id ~ now ~ createPayment)
      } yield payment

    override def get(searchFilters: SearchFilters): F[List[Payment]] = {
      val query = PaymentsSql.select(searchFilters).paginateOpt(searchFilters.limit, searchFilters.page)
      query.fragment.query(PaymentsSql.decoder).queryList(query.argument)

    }

  }
}
