package babymed.services.users.domain

import java.time.LocalDate
import java.time.LocalDateTime

import babymed.refinements.Phone
import babymed.services.users.domain.types._
import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import io.circe.refined._

@derive(decoder, encoder)
case class Customer(
    id: CustomerId,
    createdAt: LocalDateTime,
    firstname: FirstName,
    lastname: LastName,
    regionId: RegionId,
    townId: TownId,
    address: Address,
    birthday: LocalDate,
    phone: Phone,
  )

object Customer {
  @derive(decoder, encoder)
  case class CreateCustomer(
      firstname: FirstName,
      lastname: LastName,
      regionId: RegionId,
      townId: TownId,
      address: Address,
      birthday: LocalDate,
      phone: Phone,
    )

  @derive(decoder, encoder)
  case class CustomerWithAddress(
      id: CustomerId,
      createdAt: LocalDateTime,
      firstname: FirstName,
      lastname: LastName,
      regionId: RegionId,
      townId: TownId,
      address: Address,
      birthday: LocalDate,
      phone: Phone,
      region: Region,
      town: Town,
    )
}
