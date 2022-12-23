package babymed.exception

import scala.util.control.NoStackTrace

case class UpdatePaymentStatusError(phone: String) extends NoStackTrace
