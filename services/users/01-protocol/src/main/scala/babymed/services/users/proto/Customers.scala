package babymed.services.users.proto

import babymed.services.users.domain.types.{CustomerId, RegionId}
import babymed.services.users.domain.{CreateCustomer, Customer, CustomerWithAddress, Region, SearchFilters, Town}
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec

@service(Custom)
trait Customers[F[_]] {
  def createCustomers(createCustomer: CreateCustomer): F[Customer]
  def getCustomerById(customerId: CustomerId): F[Option[CustomerWithAddress]]
  def getCustomers(searchFilters: SearchFilters): F[List[CustomerWithAddress]]
  def getTotalCustomers(searchFilters: SearchFilters): F[Long]
  def getRegions: F[List[Region]]
  def getTownsByRegionId(regionId: RegionId): F[List[Town]]
}

object Customers {}
