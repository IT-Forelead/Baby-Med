package babymed.domain

import scala.collection.immutable

import enumeratum.values.StringCirceEnum
import enumeratum.values.StringEnum
import enumeratum.values.StringEnumEntry

sealed abstract class MessageType(val value: String) extends StringEnumEntry

object MessageType extends StringCirceEnum[MessageType] with StringEnum[MessageType] {
  case object Registration extends MessageType("registration")

  override def values: immutable.IndexedSeq[MessageType] = findValues
  def find(value: String): Option[MessageType] = values.find(_.value == value)
}
