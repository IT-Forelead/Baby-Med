package babymed.services.payments.repositories

import java.time.LocalDateTime
import java.util.UUID

import cats.effect.IO
import cats.effect.Resource
import cats.implicits._
import org.scalacheck.Gen
import skunk.Session
import skunk.implicits.toIdOps

import babymed.services.payments.generators.PaymentGenerator
import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.types.CustomerId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId
import babymed.services.users.repositories.sql.CustomersSql
import babymed.support.skunk.syntax.all._

object data extends PaymentGenerator {
  implicit private def gen2instance[T](gen: Gen[T]): T = gen.sample.get

  object customer {
    val id1: CustomerId = customerIdGen.get
    val data1: CreateCustomer = createCustomerGen
      .copy(
        townId = TownId(UUID.fromString("0d073b76-08ce-4b78-a88c-a0cb6f80eaf9")),
        regionId = RegionId(UUID.fromString("ad514b71-3096-4be5-a455-d87abbb081b2")),
      )
    val values: Map[CustomerId, CreateCustomer] = Map(id1 -> data1)
  }

  def setup(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    setupCustomers

  private def setupCustomers(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    customer.values.toList.traverse_ {
      case id -> data =>
        CustomersSql.insert.queryUnique(id ~ LocalDateTime.now() ~ data)
    }
}
