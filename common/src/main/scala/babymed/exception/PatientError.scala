package babymed.exception

sealed trait PatientError extends BabyMedError

object PatientError {
  final case class CustomerPhoneInUse(cause: String) extends PatientError
}
