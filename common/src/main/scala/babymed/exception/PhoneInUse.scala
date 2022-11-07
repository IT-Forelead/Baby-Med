package babymed.exception

import babymed.refinements.Phone

import scala.util.control.NoStackTrace

case class PhoneInUse(phone: Phone) extends NoStackTrace
