package babymed.services.visits.repositories.sql

import java.time.LocalDateTime

import skunk._
import skunk.codec.all._
import skunk.implicits._

import babymed.services.users.domain.City
import babymed.services.users.domain.Patient
import babymed.services.users.domain.Region
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.PatientVisitId
import babymed.support.skunk.codecs.phone
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps

object VisitsSql {
  private val Columns = patientVisitId ~ timestamp ~ patientId ~ serviceId ~ paymentStatus ~ bool
  private val ColumnsWithoutPaymentStatus =
    patientVisitId ~ timestamp ~ patientId ~ serviceId
  private val PatientColumns =
    patientId ~ timestamp ~ firstName ~ lastName ~ regionId ~ cityId ~ address.opt ~ date ~ phone ~ bool

  val encoder: Encoder[PatientVisitId ~ LocalDateTime ~ CreatePatientVisit] =
    ColumnsWithoutPaymentStatus.contramap {
      case id ~ createAt ~ cpv =>
        id ~ createAt ~ cpv.patientId ~ cpv.serviceId
    }

  val decoder: Decoder[PatientVisit] = Columns.map {
    case id ~ createdAt ~ patientId ~ serviceId ~ paymentStatus ~ _ =>
      PatientVisit(id, createdAt, patientId, serviceId, paymentStatus)
  }

  val decPatient: Decoder[Patient] = PatientColumns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ regionId ~ cityId ~ address ~ birthday ~ phone ~ _ =>
      Patient(id, createdAt, firstName, lastName, regionId, cityId, address, birthday, phone)
  }

  val decRegion: Decoder[Region] = (regionId ~ regionName ~ bool).map {
    case id ~ name ~ _ =>
      Region(id, name)
  }

  val decCity: Decoder[City] = (cityId ~ cityName ~ regionId ~ bool).map {
    case id ~ name ~ regionId ~ _ =>
      City(id, regionId, name)
  }

  val decServiceWithTypeName: Decoder[ServiceWithTypeName] =
    (serviceId ~ serviceTypeId ~ serviceName ~ price ~ bool ~ serviceTypeName).map {
      case id ~ serviceTypeId ~ name ~ price ~ _ ~ serviceTypeName =>
        ServiceWithTypeName(id, serviceTypeId, name, price, serviceTypeName)
    }

  val decPaymentVisitInfo: Decoder[PatientVisitInfo] =
    (decoder ~ decPatient ~ decServiceWithTypeName ~ decRegion ~ decCity).map {
      case visit ~ patient ~ serviceWithTypeName ~ region ~ city =>
        PatientVisitInfo(visit, patient, serviceWithTypeName, region, city)
    }

  val insert: Query[PatientVisitId ~ LocalDateTime ~ CreatePatientVisit, PatientVisit] =
    sql"""INSERT INTO visits VALUES ($encoder) RETURNING *""".query(decoder)

  private def searchFilter(filters: PatientVisitFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"visits.created_at >= $timestamp"),
      filters.endDate.map(sql"visits.created_at <= $timestamp"),
      filters.patientId.map(sql"visits.patient_id = $patientId"),
      filters.serviceId.map(sql"visits.service_id = $serviceId"),
      filters.paymentStatus.map(sql"visits.payment_status = $paymentStatus"),
    )

  def select(filters: PatientVisitFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT visits.*, patients.*, services.*, service_types.name, regions.*, cities.*
        FROM visits
        INNER JOIN patients ON visits.patient_id = patients.id
        INNER JOIN services ON visits.service_id = services.id
        INNER JOIN service_types ON services.service_type_id = service_types.id
        INNER JOIN regions ON patients.region_id = regions.id
        INNER JOIN cities  on patients.city_id = cities.id
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
