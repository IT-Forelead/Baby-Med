package babymed.services.payments.domain

import java.util.UUID

import babymed.effects.uuid
import derevo.cats.eqv
import derevo.cats.show
import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import io.estatico.newtype.macros.newtype
import squants.market.Currency

object types {
  object UZS extends Currency("UZS", "Uzbek sum", "SUM", 2)

  @derive(decoder, encoder, eqv, show, uuid)
  @newtype case class PaymentId(value: UUID)
}
