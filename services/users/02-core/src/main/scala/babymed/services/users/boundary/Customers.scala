package babymed.services.users.boundary

import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.Customer
import babymed.services.users.domain.CustomerFilters
import babymed.services.users.domain.CustomerWithAddress
import babymed.services.users.domain.Region
import babymed.services.users.domain.Town
import babymed.services.users.domain.types.CustomerId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.proto
import babymed.services.users.repositories.CustomersRepository

class Customers[F[_]](customersRepository: CustomersRepository[F]) extends proto.Customers[F] {
  override def createCustomers(createCustomer: CreateCustomer): F[Customer] =
    customersRepository.create(createCustomer)
  override def getCustomerById(customerId: CustomerId): F[Option[CustomerWithAddress]] =
    customersRepository.getCustomerById(customerId)
  override def getCustomers(searchFilters: CustomerFilters): F[List[CustomerWithAddress]] =
    customersRepository.get(searchFilters)
  override def getTotalCustomers(searchFilters: CustomerFilters): F[Long] =
    customersRepository.getTotal(searchFilters)
  override def getRegions: F[List[Region]] =
    customersRepository.getRegions
  override def getTownsByRegionId(regionId: RegionId): F[List[Town]] =
    customersRepository.getTownsByRegionId(regionId)
}
