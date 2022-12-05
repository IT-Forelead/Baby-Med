package babymed.services.users.domain

import java.time.LocalDate
import java.time.LocalDateTime

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import io.circe.refined._

import babymed.refinements.Phone
import babymed.services.users.domain.types._

@derive(decoder, encoder)
case class Patient(
    id: PatientId,
    createdAt: LocalDateTime,
    firstname: FirstName,
    lastname: LastName,
    regionId: RegionId,
    cityId: CityId,
    address: Option[Address],
    birthday: LocalDate,
    phone: Phone,
  )
