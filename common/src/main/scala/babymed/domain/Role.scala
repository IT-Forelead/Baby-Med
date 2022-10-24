package babymed.domain

import enumeratum.values.{StringCirceEnum, StringEnum, StringEnumEntry}
import io.circe.Decoder

import scala.collection.immutable

sealed abstract class Role(val value: String) extends StringEnumEntry

object Role extends StringCirceEnum[Role] with StringEnum[Role] {
  case object SuperManager extends Role("super_manager")
  case object TechAdmin extends Role("tech_admin")
  case object Admin extends Role("admin")

  override def values: immutable.IndexedSeq[Role] = findValues
  implicit val circeDecoderWithDefault: Decoder[Role] = circeDecoder.or(Decoder.decodeString)
  def find(value: String): Option[Role] = values.find(_.value == value)
}
