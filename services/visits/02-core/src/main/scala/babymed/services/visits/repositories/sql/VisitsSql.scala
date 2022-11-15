package babymed.services.visits.repositories.sql

import java.time.LocalDateTime

import skunk._
import skunk.codec.all.date
import skunk.codec.all.timestamp
import skunk.implicits._

import babymed.services.users.domain.Patient
import babymed.services.users.domain.Region
import babymed.services.users.domain.Town
import babymed.services.users.domain.User
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.domain.PatientVisitInfo
import babymed.services.visits.domain.types.PatientVisitId
import babymed.support.skunk.codecs.phone
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps

object VisitsSql {
  private val Columns = patientVisitId ~ timestamp ~ patientId ~ userId ~ serviceId ~ paymentStatus
  private val ColumnsWithoutPaymentStatus =
    patientVisitId ~ timestamp ~ patientId ~ userId ~ serviceId
  private val UserColumns = userId ~ timestamp ~ firstName ~ lastName ~ phone ~ role
  private val PatientColumns =
    patientId ~ timestamp ~ firstName ~ lastName ~ regionId ~ townId ~ address ~ date ~ phone

  val encoder: Encoder[PatientVisitId ~ LocalDateTime ~ CreatePatientVisit] =
    ColumnsWithoutPaymentStatus.contramap {
      case id ~ createAt ~ cpv =>
        id ~ createAt ~ cpv.patientId ~ cpv.userId ~ cpv.serviceId
    }

  val decoder: Decoder[PatientVisit] = Columns.map {
    case id ~ createAt ~ patientId ~ userId ~ serviceId ~ paymentStatus =>
      PatientVisit(id, createAt, patientId, userId, serviceId, paymentStatus)
  }

  val decPatient: Decoder[Patient] = PatientColumns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ regionId ~ townId ~ address ~ birthday ~ phone =>
      Patient(id, createdAt, firstName, lastName, regionId, townId, address, birthday, phone)
  }

  val decUser: Decoder[User] = UserColumns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ phone ~ role =>
      User(id, createdAt, firstName, lastName, phone, role)
  }

  val decRegion: Decoder[Region] = (regionId ~ regionName).map {
    case id ~ name =>
      Region(id, name)
  }

  val decTown: Decoder[Town] = (townId ~ regionId ~ townName).map {
    case id ~ regionId ~ name =>
      Town(id, regionId, name)
  }

  val decPaymentVisitInfo: Decoder[PatientVisitInfo] =
    (decoder ~ decPatient ~ decUser ~ ServicesSql.decoder ~ decRegion ~ decTown).map {
      case visit ~ patient ~ user ~ service ~ region ~ town =>
        PatientVisitInfo(visit, patient, user, service, region, town)
    }

  val insert: Query[PatientVisitId ~ LocalDateTime ~ CreatePatientVisit, PatientVisit] =
    sql"""INSERT INTO visits VALUES ($encoder)
         RETURNING id, created_at, user_id, patient_id, service_id, payment_status"""
      .query(decoder)

  private def searchFilter(filters: PatientVisitFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"visits.created_at >= $timestamp"),
      filters.endDate.map(sql"visits.created_at <= $timestamp"),
      filters.userId.map(sql"visits.user_id = $userId"),
      filters.patientId.map(sql"visits.patient_id = $patientId"),
      filters.serviceId.map(sql"visits.service_id = $serviceId"),
      filters.paymentStatus.map(sql"visits.payment_status = $paymentStatus"),
    )

  def select(filters: PatientVisitFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT
       visits.id, visits.created_at, visits.user_id, visits.patient_id, visits.service_id, visits.payment_status,
       patients.id, patients.created_at, patients.firstname, patients.lastname, patients.region_id, patients.town_id, patients.address, patients.birthday, patients.phone,
       users.id, users.created_at, users.firstname, users.lastname, users.phone, users.role,
       services.id, services.name, services.cost,
       regions.id, regions.name,
       towns.id, towns.region_id, towns.name
        FROM visits
        INNER JOIN patients ON visits.patient_id = patients.id
        INNER JOIN users ON visits.user_id = users.id
        INNER JOIN services ON visits.service_id = services.id
        INNER JOIN regions ON patients.region_id = regions.id
        INNER JOIN towns ON patients.town_id = towns.id
        WHERE visits.deleted = false"""

    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  def total(filters: PatientVisitFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT count(*) FROM visits WHERE deleted = false"""
    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  val updatePaymentStatusSql: Command[PatientVisitId] =
    sql"""UPDATE visits SET payment_status = 'fully_paid' WHERE id = $patientVisitId""".command
}
