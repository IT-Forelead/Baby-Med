package babymed.services.payments.repositories.sql

import babymed.services.payments.domain.{CreatePayment, Payment, SearchFilters}
import babymed.services.payments.domain.types.PaymentId
import babymed.services.users.domain.types.CustomerId
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps
import skunk._
import skunk.codec.all.timestamp
import skunk.implicits._

import java.time.LocalDateTime

object PaymentsSql {
  val paymentId: Codec[PaymentId] = identity[PaymentId]
  val customerId: Codec[CustomerId] = identity[CustomerId]

  private val Columns = paymentId ~ timestamp ~ customerId ~ price

  val encoder: Encoder[PaymentId ~ LocalDateTime ~ CreatePayment] = Columns.contramap {
    case id ~ createdAt ~ cp =>
      id ~ createdAt ~ cp.customerId ~ cp.price
  }

  val decoder: Decoder[Payment] = Columns.map {
    case id ~ createdAt ~ customerId ~ price =>
      Payment(id, createdAt, customerId, price)
  }

  val insert: Query[PaymentId ~ LocalDateTime ~ CreatePayment, Payment] =
    sql"""INSERT INTO payments VALUES ($encoder) RETURNING id, created_at, customer_id, price""".query(decoder)

  private def searchFilter(filters: SearchFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"created_at >= $timestamp"),
      filters.endDate.map(sql"created_at <= $timestamp")
    )

  def select(filters: SearchFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] = sql"""SELECT id, created_at, customer_id, price FROM payments"""
    baseQuery(Void).whereAndOpt(searchFilter(filters): _*)
  }

}
