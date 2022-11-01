package babymed.support.mailer.data

import ciris.refined._
import derevo.cats.show
import derevo.circe.magnolia.decoder
import derevo.derive
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.refined._
import io.estatico.newtype.macros.newtype

import babymed.util.configDecoder

object types {
  @newtype case class Subtype(value: String)
  @newtype case class Protocol(value: String)
  @newtype case class Subject(value: NonEmptyString)

  @derive(decoder, configDecoder, show)
  @newtype case class Host(value: NonEmptyString)

  @derive(decoder, configDecoder, show)
  @newtype case class Password(value: NonEmptyString)

  object Subtype {
    val HTML: Subtype = Subtype("html")
    val PLAIN: Subtype = Subtype("plain")
  }
  object Protocol {
    val Smtp: Protocol = Protocol("smtp")
    val Imap: Protocol = Protocol("imap")
  }
}
