package babymed.exception

import scala.util.control.NoStackTrace

import babymed.refinements.Phone

case class PhoneInUse(phone: Phone) extends NoStackTrace
