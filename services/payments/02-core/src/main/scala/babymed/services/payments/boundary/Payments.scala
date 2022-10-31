package babymed.services.payments.boundary

import babymed.services.payments.domain.{CreatePayment, Payment, SearchFilters}
import babymed.services.payments.proto
import babymed.services.payments.repositories.PaymentsRepository

class Payments[F[_]](paymentsRepository: PaymentsRepository[F]) extends proto.Payments[F] {

  override def create(createPayment: CreatePayment): F[Payment] =
    paymentsRepository.create(createPayment)
  override def get(searchFilters: SearchFilters): F[List[Payment]] =
    paymentsRepository.get(searchFilters)

}