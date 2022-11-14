package babymed.services.payments.repositories.sql

import java.time.LocalDateTime

import skunk._
import skunk.codec.all.date
import skunk.codec.all.timestamp
import skunk.implicits._

import babymed.services.payments.domain.CreatePayment
import babymed.services.payments.domain.Payment
import babymed.services.payments.domain.PaymentFilters
import babymed.services.payments.domain.PaymentWithPatient
import babymed.services.payments.domain.types.PaymentId
import babymed.services.users.domain.Patient
import babymed.services.users.domain.types.PatientId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId
import babymed.support.skunk.codecs.phone
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps

object PaymentsSql {
  val paymentId: Codec[PaymentId] = identity[PaymentId]
  val patientId: Codec[PatientId] = identity[PatientId]
  val regionId: Codec[RegionId] = identity[RegionId]
  val townId: Codec[TownId] = identity[TownId]

  private val Columns = paymentId ~ timestamp ~ patientId ~ price
  private val CustomerColumns =
    patientId ~ timestamp ~ firstName ~ lastName ~ regionId ~ townId ~ address ~ date ~ phone

  val encoder: Encoder[PaymentId ~ LocalDateTime ~ CreatePayment] = Columns.contramap {
    case id ~ createdAt ~ cp =>
      id ~ createdAt ~ cp.customerId ~ cp.price
  }

  val decoder: Decoder[Payment] = Columns.map {
    case id ~ createdAt ~ patientId ~ price =>
      Payment(id, createdAt, patientId, price)
  }

  val decCustomer: Decoder[Patient] = CustomerColumns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ regionId ~ townId ~ address ~ birthday ~ phone =>
      Patient(id, createdAt, firstName, lastName, regionId, townId, address, birthday, phone)
  }

  val decPaymentWithCustomer: Decoder[PaymentWithPatient] = (decoder ~ decCustomer).map {
    case payment ~ customer =>
      PaymentWithPatient(payment, customer)
  }

  val insert: Query[PaymentId ~ LocalDateTime ~ CreatePayment, Payment] =
    sql"""INSERT INTO payments VALUES ($encoder) RETURNING id, created_at, patient_id, price"""
      .query(decoder)

  private def searchFilter(filters: PaymentFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"payments.created_at >= $timestamp"),
      filters.endDate.map(sql"payments.created_at <= $timestamp"),
    )

  def select(filters: PaymentFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT payments.id, payments.created_at, payments.patient_id, payments.price, patients.id,
         patients.created_at,
         patients.firstname,
         patients.lastname,
         patients.region_id,
         patients.town_id,
         patients.address,
         patients.birthday,
         patients.phone FROM payments
       INNER JOIN patients ON payments.patient_id = patients.id
       WHERE payments.deleted = false"""
    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  def total(filters: PaymentFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] = sql"""SELECT count(*) FROM payments WHERE deleted = false"""
    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  val deleteSql: Command[PaymentId] =
    sql"""UPDATE payments SET deleted = true WHERE id = $paymentId""".command
}
