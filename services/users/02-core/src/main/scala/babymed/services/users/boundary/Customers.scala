package babymed.services.users.boundary

import babymed.services.users.domain.{CreateCustomer, Customer, CustomerWithAddress, SearchFilters}
import babymed.services.users.proto
import babymed.services.users.repositories.CustomersRepository

class Customers[F[_]](customersRepository: CustomersRepository[F]) extends proto.Customers[F] {

  override def createCustomers(createCustomer: CreateCustomer): F[Customer] = {
    customersRepository.create(createCustomer)
  }

  override def getCustomers(searchFilters: SearchFilters): F[List[CustomerWithAddress]] =
    customersRepository.get(searchFilters)

  override def getTotalCustomers(searchFilters: SearchFilters): F[Long] =
    customersRepository.getTotal(searchFilters)

}
