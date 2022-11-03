package babymed.domain

import scala.collection.immutable

import enumeratum.values.StringCirceEnum
import enumeratum.values.StringEnum
import enumeratum.values.StringEnumEntry

sealed abstract class Role(val value: String) extends StringEnumEntry

object Role extends StringCirceEnum[Role] with StringEnum[Role] {
  case object SuperManager extends Role("super_manager")
  case object TechAdmin extends Role("tech_admin")
  case object Admin extends Role("admin")
  case object Doctor extends Role("doctor")

  override def values: immutable.IndexedSeq[Role] = findValues
  def find(value: String): Option[Role] = values.find(_.value == value)
}
