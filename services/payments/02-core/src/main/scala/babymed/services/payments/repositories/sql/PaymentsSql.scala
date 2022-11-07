package babymed.services.payments.repositories.sql

import java.time.LocalDateTime

import skunk._
import skunk.codec.all.date
import skunk.codec.all.timestamp
import skunk.implicits._

import babymed.services.payments.domain.CreatePayment
import babymed.services.payments.domain.Payment
import babymed.services.payments.domain.PaymentWithCustomer
import babymed.services.payments.domain.SearchFilters
import babymed.services.payments.domain.types.PaymentId
import babymed.services.users.domain.Customer
import babymed.services.users.domain.types.CustomerId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId
import babymed.support.skunk.codecs.phone
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps

object PaymentsSql {
  val paymentId: Codec[PaymentId] = identity[PaymentId]
  val customerId: Codec[CustomerId] = identity[CustomerId]
  val regionId: Codec[RegionId] = identity[RegionId]
  val townId: Codec[TownId] = identity[TownId]

  private val Columns = paymentId ~ timestamp ~ customerId ~ price
  private val CustomerColumns =
    customerId ~ timestamp ~ firstName ~ lastName ~ regionId ~ townId ~ address ~ date ~ phone

  val encoder: Encoder[PaymentId ~ LocalDateTime ~ CreatePayment] = Columns.contramap {
    case id ~ createdAt ~ cp =>
      id ~ createdAt ~ cp.customerId ~ cp.price
  }

  val decoder: Decoder[Payment] = Columns.map {
    case id ~ createdAt ~ customerId ~ price =>
      Payment(id, createdAt, customerId, price)
  }

  val decCustomer: Decoder[Customer] = CustomerColumns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ regionId ~ townId ~ address ~ birthday ~ phone =>
      Customer(id, createdAt, firstName, lastName, regionId, townId, address, birthday, phone)
  }

  val decPaymentWithCustomer: Decoder[PaymentWithCustomer] = (decoder ~ decCustomer).map {
    case payment ~ customer =>
      PaymentWithCustomer(payment, customer)
  }

  val insert: Query[PaymentId ~ LocalDateTime ~ CreatePayment, Payment] =
    sql"""INSERT INTO payments VALUES ($encoder) RETURNING id, created_at, customer_id, price"""
      .query(decoder)

  private def searchFilter(filters: SearchFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"payments.created_at >= $timestamp"),
      filters.endDate.map(sql"payments.created_at <= $timestamp"),
    )

  def select(filters: SearchFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT payments.id, payments.created_at, payments.customer_id, payments.price, customers.id,
         customers.created_at,
         customers.firstname,
         customers.lastname,
         customers.region_id,
         customers.town_id,
         customers.address,
         customers.birthday,
         customers.phone FROM payments
       INNER JOIN customers ON payments.customer_id = customers.id
       WHERE payments.deleted = false"""
    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  def total(filters: SearchFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] = sql"""SELECT count(*) FROM payments WHERE deleted = false"""
    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  val deleteSql: Command[PaymentId] =
    sql"""UPDATE payments SET deleted = true WHERE id = $paymentId""".command
}
