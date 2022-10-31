package babymed.services.payments.proto

import babymed.services.payments.domain.{CreatePayment, Payment, PaymentWithCustomer, SearchFilters}
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec
import higherkindness.mu.rpc.protocol.Custom

@service(Custom)
trait Payments[F[_]] {
  def create(createPayment: CreatePayment): F[Payment]
  def get(searchFilters: SearchFilters): F[List[PaymentWithCustomer]]
}

object Payments {}
