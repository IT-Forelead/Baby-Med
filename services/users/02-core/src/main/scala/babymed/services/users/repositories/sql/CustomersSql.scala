package babymed.services.users.repositories.sql

import java.time.LocalDateTime

import skunk._
import skunk.codec.all._
import skunk.implicits._

import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.Customer
import babymed.services.users.domain.CustomerWithAddress
import babymed.services.users.domain.Region
import babymed.services.users.domain.SearchFilters
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

  private def searchFilter(filters: SearchFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"customers.created_at >= $zonedDateTime"),
      filters.endDate.map(sql"customers.created_at <= $zonedDateTime"),
      filters.customerFirstName.map(sql"customers.firstname like $firstName"),
      filters.customerLastName.map(sql"customers.lastname like $lastName"),
      filters.regionId.map(sql"customers.region_id = $regionId"),
      filters.townId.map(sql"customers.town_id = $townId"),
      filters.address.map(sql"customers.address like $address"),
      filters.birthday.map(sql"customers.birthday = $date"),
      filters.phone.map(sql"customers.phone like $phone"),
    )

  def select(filters: SearchFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT
       customers.id,
       customers.created_at,
       customers.firstname,
       customers.lastname,
       customers.region_id,
       customers.town_id,
       customers.address,
       customers.birthday,
       customers.phone,
       regions.id,
       regions.name,
       towns.id,
       towns.region_id,
       towns.name
        FROM customers
        INNER JOIN regions ON customers.region_id = regions.id
        INNER JOIN towns ON customers.town_id = towns.id
        WHERE customers.deleted = false"""

    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  def total(filters: SearchFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT count(*) FROM customers WHERE deleted = false"""
    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  val selectById: Query[CustomerId, CustomerWithAddress] =
    sql"""SELECT
       customers.id,
       customers.created_at,
       customers.firstname,
       customers.lastname,
       customers.region_id,
       customers.town_id,
       customers.address,
       customers.birthday,
       customers.phone,
       regions.id,
       regions.name,
       towns.id,
       towns.region_id,
       towns.name
        FROM customers
        INNER JOIN regions ON customers.region_id = regions.id
        INNER JOIN towns ON customers.town_id = towns.id
        WHERE customers.id = $customerId AND customers.deleted = false"""
      .query(decCustomerWithAddress)

  val insert: Query[CustomerId ~ LocalDateTime ~ CreateCustomer, Customer] =
    sql"""INSERT INTO customers VALUES ($encoder)
         RETURNING id, created_at, firstname, lastname, region_id, town_id, address, birthday, phone"""
      .query(decoder)

  val selectRegions: Query[Void, Region] =
    sql"""SELECT id, name FROM regions WHERE deleted = false ORDER BY name ASC"""
      .query(decRegion)

  val selectTownsByRegionId: Query[RegionId, Town] =
    sql"""SELECT id, region_id, name FROM towns WHERE region_id = $regionId AND deleted = false ORDER BY name ASC"""
      .query(decTown)
}
