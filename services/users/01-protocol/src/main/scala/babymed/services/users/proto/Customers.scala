package babymed.services.users.proto

import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.Customer
import babymed.services.users.domain.CustomerWithAddress
import babymed.services.users.domain.SearchFilters
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec
import higherkindness.mu.rpc.protocol.Custom

@service(Custom)
trait Customers[F[_]] {
  def createCustomers(createCustomer: CreateCustomer): F[Customer]
  def getCustomers(searchFilters: SearchFilters): F[List[CustomerWithAddress]]
  def getTotalCustomers(searchFilters: SearchFilters): F[Long]
}

object Customers {}
