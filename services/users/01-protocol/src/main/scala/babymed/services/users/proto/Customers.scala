package babymed.services.users.proto

import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.Customer
import babymed.services.users.domain.CustomerWithAddress
import babymed.services.users.domain.Region
import babymed.services.users.domain.SearchFilters
import babymed.services.users.domain.Town
import babymed.services.users.domain.types.CustomerId
import babymed.services.users.domain.types.RegionId
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
