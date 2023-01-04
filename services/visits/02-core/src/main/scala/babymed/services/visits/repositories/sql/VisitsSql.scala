package babymed.services.visits.repositories.sql

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
  private val Columns = patientVisitId ~ timestamp ~ userId ~ patientId ~ paymentStatus ~ bool
  private val ItemColumns = patientVisitId ~ serviceId ~ bool
  private val ItemsColumns = patientVisitId ~ serviceId
  private val PatientColumns =
    patientId ~ timestamp ~ firstName ~ lastName ~ regionId ~ cityId ~ address.opt ~ date ~ phone ~ bool

  val encoder: Encoder[PatientVisit] =
    Columns.contramap(visit =>
      visit.id ~ visit.createdAt ~ visit.userId ~ visit.patientId ~ visit.paymentStatus ~ false
    )

  val encItem: Encoder[PatientVisitItem] =
    ItemsColumns.contramap(oei => oei.visitId ~ oei.serviceId)

  val decoder: Decoder[PatientVisit] = Columns.map {
    case id ~ createdAt ~ userId ~ patientId ~ paymentStatus ~ _ =>
      PatientVisit(id, createdAt, userId, patientId, paymentStatus)
  }

  val decVisitItem: Decoder[VisitItem] = ItemColumns.map {
    case visitId ~ serviceId ~ _ =>
      VisitItem(visitId, serviceId)
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
    (decoder ~ firstName ~ lastName ~ decPatient ~ decRegion ~ decCity)
      .map {
        case visit ~ firstname ~ lastname ~ patient ~ region ~ city =>
          PatientVisitInfo(visit, firstname, lastname, patient, region, city)
      }

  val insert: Query[PatientVisit, PatientVisit] =
    sql"""INSERT INTO visits VALUES ($encoder) RETURNING *""".query(decoder)

  def insertItems(item: List[PatientVisitItem]): Command[item.type] = {
    val enc = encItem.values.list(item)
    sql"""INSERT INTO visit_items VALUES $enc""".command
  }

  private def searchFilter(filters: PatientVisitFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"visits.created_at >= $timestamp"),
      filters.endDate.map(sql"visits.created_at <= $timestamp"),
      filters.userId.map(sql"visits.user_id = $userId"),
      filters.patientId.map(sql"visits.patient_id = $patientId"),
      filters.serviceId.map(sql"visits.service_id = $serviceId"),
      filters.serviceTypeId.map(sql"services.service_type_id = $serviceTypeId"),
      filters.paymentStatus.map(sql"visits.payment_status = $paymentStatus"),
    )

  def select(filters: PatientVisitFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT visits.*, users.firstname, users.lastname, patients.*, regions.*, cities.*
        FROM visits
        INNER JOIN users ON visits.user_id = users.id
        INNER JOIN patients ON visits.patient_id = patients.id
        INNER JOIN regions ON patients.region_id = regions.id
        INNER JOIN cities  on patients.city_id = cities.id
        WHERE visits.deleted = false"""

    baseQuery(Void).andOpt(searchFilter(filters): _*) |+| sql" ORDER BY visits.created_at DESC"
      .apply(Void)
  }

  def total(filters: PatientVisitFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT count(*) FROM visits WHERE deleted = false"""
    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  val updatePaymentStatusSql: Query[PatientVisitId, PatientVisit] =
    sql"""UPDATE visits SET payment_status = 'fully_paid' WHERE id = $patientVisitId RETURNING *"""
      .query(decoder)

  val selectItemsSql: Query[PatientVisitId, ServiceWithTypeName] =
    sql"""SELECT services.*, service_types.name
        FROM visit_items
        INNER JOIN services ON visit_items.service_id = services.id
        INNER JOIN service_types ON services.service_type_id = service_types.id
        WHERE visit_items.visit_id = $patientVisitId
        AND visit_items.deleted = false"""
      .query(decServiceWithTypeName)

  val selectItemsByVisitIdSql: Query[PatientVisitId, VisitItem] =
    sql"""SELECT * FROM visit_items WHERE visit_id = $patientVisitId AND deleted = false"""
      .query(decVisitItem)
}
