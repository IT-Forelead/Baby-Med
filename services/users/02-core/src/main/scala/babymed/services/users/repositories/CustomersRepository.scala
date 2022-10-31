package babymed.services.users.repositories

import babymed.domain.ID
import babymed.effects.Calendar
import babymed.effects.GenUUID
import babymed.exception.CustomerError
import babymed.services.users.domain.{CreateCustomer, Customer, CustomerWithAddress, SearchFilters}
import babymed.services.users.domain.types.CustomerId
import babymed.services.users.repositories.sql.CustomersSql
import babymed.support.skunk.syntax.all._
import cats.effect.Concurrent
import cats.effect.MonadCancel
import cats.effect.Resource
import cats.implicits._
import skunk.Session
import skunk._
import skunk.codec.all.int8
import skunk.implicits.toIdOps

trait CustomersRepository[F[_]] {
  def create(createCustomer: CreateCustomer): F[Customer]
  def get(filters: SearchFilters): F[List[CustomerWithAddress]]
  def getTotal(filters: SearchFilters): F[Long]
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

    override def get(
        filters: SearchFilters
      ): F[List[CustomerWithAddress]] = {
      val query = CustomersSql.select(filters).paginateOpt(filters.limit, filters.page)
      query.fragment.query(CustomersSql.decCustomerWithAddress).queryList(query.argument)
    }

    override def getTotal(filters: SearchFilters): F[Long] = {
      val query = CustomersSql.total(filters)
      query.fragment.query(int8).queryUnique(query.argument)
    }
  }
}
