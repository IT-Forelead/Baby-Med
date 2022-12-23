package babymed.services.visits.repositories.sql

import skunk._
import skunk.codec.all._
import skunk.implicits._

import babymed.services.users.domain.City
import babymed.services.users.domain.Patient
import babymed.services.users.domain.Region
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.ChequeId
import babymed.support.skunk.codecs.phone
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps

object VisitsSql {
  private val Columns =
    patientVisitId ~ timestamp ~ userId ~ patientId ~ serviceId ~ chequeId ~ paymentStatus ~ bool
  private val ColumnsWithoutPaymentStatus =
    patientVisitId ~ timestamp ~ userId ~ patientId ~ serviceId ~ chequeId
  private val PatientColumns =
    patientId ~ timestamp ~ firstName ~ lastName ~ regionId ~ cityId ~ address.opt ~ date ~ phone ~ bool

  val encoder: Encoder[InsertPatientVisit] =
    ColumnsWithoutPaymentStatus.contramap { ipv =>
      ipv.id ~ ipv.createdAt ~ ipv.userId ~ ipv.patientId ~ ipv.serviceId ~ ipv.chequeId
    }

  val decoder: Decoder[PatientVisit] = Columns.map {
    case id ~ createdAt ~ userId ~ patientId ~ serviceId ~ chequeId ~ paymentStatus ~ _ =>
      PatientVisit(id, createdAt, userId, patientId, serviceId, chequeId, paymentStatus)
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
    (decoder ~ firstName ~ lastName ~ decPatient ~ decServiceWithTypeName ~ decRegion ~ decCity)
      .map {
        case visit ~ firstname ~ lastname ~ patient ~ serviceWithTypeName ~ region ~ city =>
          PatientVisitInfo(visit, firstname, lastname, patient, serviceWithTypeName, region, city)
      }
  def insertItems(item: List[InsertPatientVisit]): Command[item.type] = {
    val enc = encoder.values.list(item)
    sql"""INSERT INTO visits VALUES ($enc)""".command
  }

  private def searchFilter(filters: PatientVisitFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"visits.created_at >= $timestamp"),
      filters.endDate.map(sql"visits.created_at <= $timestamp"),
      filters.userId.map(sql"visits.user_id = $userId"),
      filters.patientId.map(sql"visits.patient_id = $patientId"),
      filters.serviceId.map(sql"visits.service_id = $serviceId"),
      filters.serviceTypeId.map(sql"services.service_type_id = $serviceTypeId"),
      filters.chequeId.map(sql"visits.cheque_id = $chequeId"),
      filters.paymentStatus.map(sql"visits.payment_status = $paymentStatus"),
    )

  def select(filters: PatientVisitFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT visits.*, users.firstname, users.lastname, patients.*, services.*, service_types.name, regions.*, cities.*
        FROM visits
        INNER JOIN users ON visits.user_id = users.id
        INNER JOIN patients ON visits.patient_id = patients.id
        INNER JOIN services ON visits.service_id = services.id
        INNER JOIN service_types ON services.service_type_id = service_types.id
        INNER JOIN regions ON patients.region_id = regions.id
        INNER JOIN cities  on patients.city_id = cities.id
        WHERE visits.deleted = false"""

    baseQuery(Void).andOpt(searchFilter(filters): _*) |+| sql" ORDER BY visits.created_at DESC"
      .apply(Void)
  }

  def total(filters: PatientVisitFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT count(*) FROM visits
        INNER JOIN services ON visits.service_id = services.id
        INNER JOIN service_types ON services.service_type_id = service_types.id
        WHERE visits.deleted = false"""
    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  val updatePaymentStatusSql: Command[ChequeId] =
    sql"""UPDATE visits SET payment_status = 'fully_paid' WHERE cheque_id = $chequeId""".command
}
