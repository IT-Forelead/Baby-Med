package babymed.services.users.proto

import babymed.services.users.domain.Customer._
import babymed.services.users.domain.{Customer, SearchFilters}
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec
import eu.timepit.refined.cats._
import higherkindness.mu.rpc.protocol.Custom
import io.circe.refined._

@service(Custom)
trait Customers[F[_]] {
  def createCustomers(createCustomer: CreateCustomer): F[Customer]
  def getCustomers(searchFilters: SearchFilters): F[List[CustomerWithAddress]]
  def getTotalCustomers(searchFilters: SearchFilters): F[Long]
}

object Customers {}
