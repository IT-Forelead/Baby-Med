package babymed.services.payments.repositories

import cats.effect.Concurrent
import cats.effect.MonadCancel
import cats.effect.Resource
import cats.implicits._
import skunk._
import skunk.codec.all.int8
import skunk.implicits._

import babymed.domain.ID
import babymed.effects.Calendar
import babymed.effects.GenUUID
import babymed.services.payments.domain.CreatePayment
import babymed.services.payments.domain.Payment
import babymed.services.payments.domain.PaymentWithCustomer
import babymed.services.payments.domain.SearchFilters
import babymed.services.payments.domain.types.PaymentId
import babymed.services.payments.repositories.sql.PaymentsSql
import babymed.support.skunk.syntax.all._

trait PaymentsRepository[F[_]] {
  def create(createPayment: CreatePayment): F[Payment]
  def get(searchFilters: SearchFilters): F[List[PaymentWithCustomer]]
  def getPaymentsTotal(filters: SearchFilters): F[Long]
  def delete(paymentId: PaymentId): F[Unit]
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

    override def get(searchFilters: SearchFilters): F[List[PaymentWithCustomer]] = {
      val query =
        PaymentsSql.select(searchFilters).paginateOpt(searchFilters.limit, searchFilters.page)
      query.fragment.query(PaymentsSql.decPaymentWithCustomer).queryList(query.argument)
    }

    override def getPaymentsTotal(filters: SearchFilters): F[Long] = {
      val query = PaymentsSql.total(filters)
      query.fragment.query(int8).queryUnique(query.argument)
    }

    override def delete(paymentId: PaymentId): F[Unit] =
      PaymentsSql.deleteSql.execute(paymentId)
  }
}
