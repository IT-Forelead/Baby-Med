package babymed.services.payments.boundary

import cats.Monad
import cats.implicits._

import babymed.domain.ResponseData
import babymed.services.payments.domain.CreatePayment
import babymed.services.payments.domain.Payment
import babymed.services.payments.domain.PaymentFilters
import babymed.services.payments.domain.PaymentWithCustomer
import babymed.services.payments.domain.types.PaymentId
import babymed.services.payments.proto
import babymed.services.payments.repositories.PaymentsRepository

class Payments[F[_]: Monad](paymentsRepository: PaymentsRepository[F]) extends proto.Payments[F] {
  override def create(createPayment: CreatePayment): F[Payment] =
    paymentsRepository.create(createPayment)
  override def get(filters: PaymentFilters): F[ResponseData[PaymentWithCustomer]] =
    for {
      payments <- paymentsRepository.get(filters)
      total <- paymentsRepository.getPaymentsTotal(filters)
    } yield ResponseData(payments, total)
  override def getPaymentsTotal(filters: PaymentFilters): F[Long] =
    paymentsRepository.getPaymentsTotal(filters)
  override def delete(paymentId: PaymentId): F[Unit] =
    paymentsRepository.delete(paymentId)
}
