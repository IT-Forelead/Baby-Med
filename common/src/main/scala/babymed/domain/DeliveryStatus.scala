package babymed.domain

import scala.collection.immutable

import enumeratum.values.StringCirceEnum
import enumeratum.values.StringEnum
import enumeratum.values.StringEnumEntry

sealed abstract class DeliveryStatus(val value: String) extends StringEnumEntry

object DeliveryStatus extends StringCirceEnum[DeliveryStatus] with StringEnum[DeliveryStatus] {
  case object Sent extends DeliveryStatus("sent")
  case object Delivered extends DeliveryStatus("delivered")
  case object Failed extends DeliveryStatus("failed")
  case object Undefined extends DeliveryStatus("undefined")

  override def values: immutable.IndexedSeq[DeliveryStatus] = findValues
  def find(value: String): Option[DeliveryStatus] = values.find(_.value == value)
}
