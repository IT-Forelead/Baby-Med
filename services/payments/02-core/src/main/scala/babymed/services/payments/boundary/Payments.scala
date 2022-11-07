package babymed.services.payments.boundary

import babymed.services.payments.domain.CreatePayment
import babymed.services.payments.domain.Payment
import babymed.services.payments.domain.PaymentWithCustomer
import babymed.services.payments.domain.SearchFilters
import babymed.services.payments.domain.types.PaymentId
import babymed.services.payments.proto
import babymed.services.payments.repositories.PaymentsRepository

class Payments[F[_]](paymentsRepository: PaymentsRepository[F]) extends proto.Payments[F] {
  override def create(createPayment: CreatePayment): F[Payment] =
    paymentsRepository.create(createPayment)
  override def get(searchFilters: SearchFilters): F[List[PaymentWithCustomer]] =
    paymentsRepository.get(searchFilters)
  override def getPaymentsTotal(searchFilters: SearchFilters): F[Long] =
    paymentsRepository.getPaymentsTotal(searchFilters)
  override def delete(paymentId: PaymentId): F[Unit] =
    paymentsRepository.delete(paymentId)
}
