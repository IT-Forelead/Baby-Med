package babymed.services.users.repositories.sql

import java.time.LocalDateTime

import skunk._
import skunk.codec.all._
import skunk.implicits._

import babymed.services.users.domain.City
import babymed.services.users.domain.CreatePatient
import babymed.services.users.domain.Patient
import babymed.services.users.domain.PatientFilters
import babymed.services.users.domain.PatientWithAddress
import babymed.services.users.domain.PatientWithName
import babymed.services.users.domain.Region
import babymed.services.users.domain.types.Fullname
import babymed.services.users.domain.types.PatientId
import babymed.services.users.domain.types.RegionId
import babymed.support.skunk.codecs._
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps

object PatientsSql {
  private val Columns =
    patientId ~ timestamp ~ firstName ~ lastName ~ regionId ~ cityId ~ address.opt ~ date ~ phone ~ bool

  val encoder: Encoder[PatientId ~ LocalDateTime ~ CreatePatient] = Columns.contramap {
    case id ~ createdAt ~ cp =>
      id ~ createdAt ~ cp.firstname ~ cp.lastname ~ cp.regionId ~ cp.cityId ~ cp.address ~ cp.birthday ~ cp.phone ~ false
  }

  val decoder: Decoder[Patient] = Columns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ regionId ~ cityId ~ address ~ birthday ~ phone ~ _ =>
      Patient(id, createdAt, firstName, lastName, regionId, cityId, address, birthday, phone)
  }

  val decRegion: Decoder[Region] = (regionId ~ regionName ~ bool).map {
    case id ~ name ~ _ =>
      Region(id, name)
  }

  val decCity: Decoder[City] = (cityId ~ cityName ~ regionId ~ bool).map {
    case id ~ regionId ~ name ~ _ =>
      City(id, name, regionId)
  }

  val decPatientWithAddress: Decoder[PatientWithAddress] = (decoder ~ decRegion ~ decCity).map {
    case patient ~ region ~ city =>
      PatientWithAddress(patient, region, city)
  }

  val decPatientWithName: Decoder[PatientWithName] =
    (patientId ~ firstName ~ lastName ~ phone).map {
      case patientId ~ firstName ~ lastName ~ phone =>
        PatientWithName(patientId, firstName, lastName, phone)
    }

  private def searchFilter(filters: PatientFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"patients.created_at >= $timestamp"),
      filters.endDate.map(sql"patients.created_at <= $timestamp"),
      filters.patientFirstName.map(sql"patients.firstname ILIKE $firstName"),
      filters.patientLastName.map(sql"patients.lastname ILIKE $lastName"),
      filters.regionId.map(sql"patients.region_id = $regionId"),
      filters.cityId.map(sql"patients.city_id = $cityId"),
      filters.address.map(sql"patients.address ILIKE $address"),
      filters.birthday.map(sql"patients.birthday = $date"),
      filters.phone.map(sql"patients.phone ILIKE $phone"),
    )

  def select(filters: PatientFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT patients.*, regions.*, cities.*
        FROM patients
        INNER JOIN regions ON patients.region_id = regions.id
        INNER JOIN cities ON patients.city_id = cities.id
        WHERE patients.deleted = false"""

    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  def total(filters: PatientFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT count(*) FROM patients WHERE deleted = false"""
    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  val getPatientsByName: Query[Fullname ~ Fullname, PatientWithName] =
    sql"""SELECT patients.id, patients.firstname, patients.lastname, patients.phone
       FROM patients WHERE patients.deleted = false AND (
           patients.firstname ILIKE $fullName OR patients.lastname ILIKE $fullName
        ) ORDER BY lastname, firstname """.query(decPatientWithName)

  val selectById: Query[PatientId, PatientWithAddress] =
    sql"""SELECT patients.*, regions.*, cities.*
        FROM patients
        INNER JOIN regions ON patients.region_id = regions.id
        INNER JOIN cities ON patients.city_id = cities.id
        WHERE patients.id = $patientId AND patients.deleted = false"""
      .query(decPatientWithAddress)

  val insert: Query[PatientId ~ LocalDateTime ~ CreatePatient, Patient] =
    sql"""INSERT INTO patients VALUES ($encoder) RETURNING *"""
      .query(decoder)

  val selectRegions: Query[Void, Region] =
    sql"""SELECT * FROM regions WHERE deleted = false ORDER BY name ASC"""
      .query(decRegion)

  val selectCitiesByRegionId: Query[RegionId, City] =
    sql"""SELECT * FROM cities WHERE region_id = $regionId AND deleted = false ORDER BY name ASC"""
      .query(decCity)
}
