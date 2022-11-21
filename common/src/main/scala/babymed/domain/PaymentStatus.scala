package babymed.domain

import scala.collection.immutable

import enumeratum.values.StringCirceEnum
import enumeratum.values.StringEnum
import enumeratum.values.StringEnumEntry

sealed abstract class PaymentStatus(val value: String) extends StringEnumEntry

object PaymentStatus extends StringCirceEnum[PaymentStatus] with StringEnum[PaymentStatus] {
  case object FullyPaid extends PaymentStatus("fully_paid")
  case object NotPaid extends PaymentStatus("not_paid")
  case object PartiallyPaid extends PaymentStatus("partially_paid")

  override def values: immutable.IndexedSeq[PaymentStatus] = findValues
  def find(value: String): Option[PaymentStatus] = values.find(_.value == value)
}
