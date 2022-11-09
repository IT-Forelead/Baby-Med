package babymed.services.users.boundary

import cats.Monad
import cats.implicits._

import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.Customer
import babymed.services.users.domain.CustomerFilters
import babymed.services.users.domain.CustomerWithAddress
import babymed.services.users.domain.CustomersWithTotal
import babymed.services.users.domain.Region
import babymed.services.users.domain.Town
import babymed.services.users.domain.types.CustomerId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.proto
import babymed.services.users.repositories.CustomersRepository

class Customers[F[_]: Monad](customersRepository: CustomersRepository[F])
    extends proto.Customers[F] {
  override def createCustomers(createCustomer: CreateCustomer): F[Customer] =
    customersRepository.create(createCustomer)
  override def getCustomerById(customerId: CustomerId): F[Option[CustomerWithAddress]] =
    customersRepository.getCustomerById(customerId)
  override def getCustomers(filters: CustomerFilters): F[CustomersWithTotal] =
    for {
      customers <- customersRepository.get(filters)
      total <- customersRepository.getTotal(filters)
    } yield CustomersWithTotal(customers, total)
  override def getTotalCustomers(filters: CustomerFilters): F[Long] =
    customersRepository.getTotal(filters)
  override def getRegions: F[List[Region]] =
    customersRepository.getRegions
  override def getTownsByRegionId(regionId: RegionId): F[List[Town]] =
    customersRepository.getTownsByRegionId(regionId)
}
