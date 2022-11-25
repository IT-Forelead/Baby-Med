package babymed.services.users.repositories.sql

import java.time.LocalDateTime

import skunk._
import skunk.codec.all._
import skunk.implicits._

import babymed.services.users.domain.CreatePatient
import babymed.services.users.domain.Patient
import babymed.services.users.domain.PatientFilters
import babymed.services.users.domain.PatientWithAddress
import babymed.services.users.domain.Region
import babymed.services.users.domain.Town
import babymed.services.users.domain.types.PatientId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId
import babymed.support.skunk.codecs._
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps

object PatientsSql {
  val patientId: Codec[PatientId] = identity[PatientId]
  val regionId: Codec[RegionId] = identity[RegionId]
  val townId: Codec[TownId] = identity[TownId]

  private val Columns =
    patientId ~ timestamp ~ firstName ~ lastName ~ regionId ~ townId ~ address.opt ~ date ~ phone

  val encoder: Encoder[PatientId ~ LocalDateTime ~ CreatePatient] = Columns.contramap {
    case id ~ createdAt ~ cp =>
      id ~ createdAt ~ cp.firstname ~ cp.lastname ~ cp.regionId ~ cp.townId ~ cp.address ~ cp.birthday ~ cp.phone
  }

  val decoder: Decoder[Patient] = Columns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ regionId ~ townId ~ address ~ birthday ~ phone =>
      Patient(id, createdAt, firstName, lastName, regionId, townId, address, birthday, phone)
  }

  val decRegion: Decoder[Region] = (regionId ~ regionName).map {
    case id ~ name =>
      Region(id, name)
  }

  val decTown: Decoder[Town] = (townId ~ regionId ~ townName).map {
    case id ~ regionId ~ name =>
      Town(id, regionId, name)
  }

  val decPatientWithAddress: Decoder[PatientWithAddress] = (decoder ~ decRegion ~ decTown).map {
    case patient ~ region ~ town =>
      PatientWithAddress(patient, region, town)
  }

  private def searchFilter(filters: PatientFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"patients.created_at >= $timestamp"),
      filters.endDate.map(sql"patients.created_at <= $timestamp"),
      filters.patientFirstName.map(sql"patients.firstname ILIKE $firstName"),
      filters.patientLastName.map(sql"patients.lastname ILIKE $lastName"),
      filters.regionId.map(sql"patients.region_id = $regionId"),
      filters.townId.map(sql"patients.town_id = $townId"),
      filters.address.map(sql"patients.address ILIKE $address"),
      filters.birthday.map(sql"patients.birthday = $date"),
      filters.phone.map(sql"patients.phone ILIKE $phone"),
    )

  def select(filters: PatientFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT
       patients.id,
       patients.created_at,
       patients.firstname,
       patients.lastname,
       patients.region_id,
       patients.town_id,
       patients.address,
       patients.birthday,
       patients.phone,
       regions.id,
       regions.name,
       towns.id,
       towns.region_id,
       towns.name
        FROM patients
        INNER JOIN regions ON patients.region_id = regions.id
        INNER JOIN towns ON patients.town_id = towns.id
        WHERE patients.deleted = false"""

    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  def total(filters: PatientFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT count(*) FROM patients WHERE deleted = false"""
    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  val selectById: Query[PatientId, PatientWithAddress] =
    sql"""SELECT
       patients.id,
       patients.created_at,
       patients.firstname,
       patients.lastname,
       patients.region_id,
       patients.town_id,
       patients.address,
       patients.birthday,
       patients.phone,
       regions.id,
       regions.name,
       towns.id,
       towns.region_id,
       towns.name
        FROM patients
        INNER JOIN regions ON patients.region_id = regions.id
        INNER JOIN towns ON patients.town_id = towns.id
        WHERE patients.id = $patientId AND patients.deleted = false"""
      .query(decPatientWithAddress)

  val insert: Query[PatientId ~ LocalDateTime ~ CreatePatient, Patient] =
    sql"""INSERT INTO patients VALUES ($encoder)
         RETURNING id, created_at, firstname, lastname, region_id, town_id, address, birthday, phone"""
      .query(decoder)

  val selectRegions: Query[Void, Region] =
    sql"""SELECT id, name FROM regions WHERE deleted = false ORDER BY name ASC"""
      .query(decRegion)

  val selectTownsByRegionId: Query[RegionId, Town] =
    sql"""SELECT id, region_id, name FROM towns WHERE region_id = $regionId AND deleted = false ORDER BY name ASC"""
      .query(decTown)
}
