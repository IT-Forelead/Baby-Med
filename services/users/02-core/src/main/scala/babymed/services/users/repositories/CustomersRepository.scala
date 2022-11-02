package babymed.services.users.repositories

import cats.effect.Concurrent
import cats.effect.MonadCancel
import cats.effect.Resource
import cats.implicits._
import skunk.Session
import skunk._
import skunk.codec.all.int8
import skunk.implicits.toIdOps

import babymed.domain.ID
import babymed.effects.Calendar
import babymed.effects.GenUUID
import babymed.exception.CustomerError
import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.Customer
import babymed.services.users.domain.CustomerWithAddress
import babymed.services.users.domain.Region
import babymed.services.users.domain.SearchFilters
import babymed.services.users.domain.Town
import babymed.services.users.domain.types.CustomerId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.repositories.sql.CustomersSql
import babymed.support.skunk.syntax.all._

trait CustomersRepository[F[_]] {
  def create(createCustomer: CreateCustomer): F[Customer]
  def getCustomerById(customerId: CustomerId): F[Option[CustomerWithAddress]]
  def get(filters: SearchFilters): F[List[CustomerWithAddress]]
  def getTotal(filters: SearchFilters): F[Long]
  def getRegions: F[List[Region]]
  def getTownsByRegionId(regionId: RegionId): F[List[Town]]
}

object CustomersRepository {
  def make[F[_]: GenUUID: Calendar: Concurrent](
      implicit
      session: Resource[F, Session[F]],
      F: MonadCancel[F, Throwable],
    ): CustomersRepository[F] = new CustomersRepository[F] {
    import sql.CustomersSql._

    override def create(createCustomer: CreateCustomer): F[Customer] =
      (for {
        id <- ID.make[F, CustomerId]
        now <- Calendar[F].currentDateTime
        customer <- insert.queryUnique(id ~ now ~ createCustomer)
      } yield customer).recoverWith {
        case SqlState.UniqueViolation(_) =>
          CustomerError
            .CustomerPhoneInUse(createCustomer.phone.value)
            .raiseError[F, Customer]
      }

    override def getCustomerById(customerId: CustomerId): F[Option[CustomerWithAddress]] =
      selectById.queryOption(customerId)

    override def get(filters: SearchFilters): F[List[CustomerWithAddress]] = {
      val query = CustomersSql.select(filters).paginateOpt(filters.limit, filters.page)
      query.fragment.query(CustomersSql.decCustomerWithAddress).queryList(query.argument)
    }

    override def getTotal(filters: SearchFilters): F[Long] = {
      val query = CustomersSql.total(filters)
      query.fragment.query(int8).queryUnique(query.argument)
    }

    override def getRegions: F[List[Region]] =
      CustomersSql.selectRegions.queryList(Void)

    override def getTownsByRegionId(regionId: RegionId): F[List[Town]] =
      CustomersSql.selectTownsByRegionId.queryList(regionId)
  }
}
