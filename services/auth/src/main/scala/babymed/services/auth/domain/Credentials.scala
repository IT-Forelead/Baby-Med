package babymed.services.auth.domain

import derevo.cats.show
import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.refined._

import babymed.refinements.Phone

@derive(decoder, encoder, show)
case class Credentials(phone: Phone, password: NonEmptyString)
