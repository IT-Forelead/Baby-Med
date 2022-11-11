package babymed.services.users.repositories.sql

import java.time.LocalDateTime

import skunk._
import skunk.codec.all._
import skunk.implicits._

import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.Customer
import babymed.services.users.domain.CustomerFilters
import babymed.services.users.domain.CustomerWithAddress
import babymed.services.users.domain.Region
import babymed.services.users.domain.Town
import babymed.services.users.domain.types.CustomerId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId
import babymed.support.skunk.codecs._
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps

object CustomersSql {
  val customerId: Codec[CustomerId] = identity[CustomerId]
  val regionId: Codec[RegionId] = identity[RegionId]
  val townId: Codec[TownId] = identity[TownId]

  private val Columns =
    customerId ~ timestamp ~ firstName ~ lastName ~ regionId ~ townId ~ address ~ date ~ phone

  val encoder: Encoder[CustomerId ~ LocalDateTime ~ CreateCustomer] = Columns.contramap {
    case id ~ createdAt ~ cc =>
      id ~ createdAt ~ cc.firstname ~ cc.lastname ~ cc.regionId ~ cc.townId ~ cc.address ~ cc.birthday ~ cc.phone
  }

  val decoder: Decoder[Customer] = Columns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ regionId ~ townId ~ address ~ birthday ~ phone =>
      Customer(id, createdAt, firstName, lastName, regionId, townId, address, birthday, phone)
  }

  val decRegion: Decoder[Region] = (regionId ~ regionName).map {
    case id ~ name =>
      Region(id, name)
  }

  val decTown: Decoder[Town] = (townId ~ regionId ~ townName).map {
    case id ~ regionId ~ name =>
      Town(id, regionId, name)
  }

  val decCustomerWithAddress: Decoder[CustomerWithAddress] = (decoder ~ decRegion ~ decTown).map {
    case customer ~ region ~ town =>
      CustomerWithAddress(customer, region, town)
  }

  private def searchFilter(filters: CustomerFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"patients.created_at >= $timestamp"),
      filters.endDate.map(sql"patients.created_at <= $timestamp"),
      filters.customerFirstName.map(sql"patients.firstname like $firstName"),
      filters.customerLastName.map(sql"patients.lastname like $lastName"),
      filters.regionId.map(sql"patients.region_id = $regionId"),
      filters.townId.map(sql"patients.town_id = $townId"),
      filters.address.map(sql"patients.address like $address"),
      filters.birthday.map(sql"patients.birthday = $date"),
      filters.phone.map(sql"patients.phone like $phone"),
    )

  def select(filters: CustomerFilters): AppliedFragment = {
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

  def total(filters: CustomerFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT count(*) FROM patients WHERE deleted = false"""
    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  val selectById: Query[CustomerId, CustomerWithAddress] =
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
        WHERE patients.id = $customerId AND patients.deleted = false"""
      .query(decCustomerWithAddress)

  val insert: Query[CustomerId ~ LocalDateTime ~ CreateCustomer, Customer] =
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
