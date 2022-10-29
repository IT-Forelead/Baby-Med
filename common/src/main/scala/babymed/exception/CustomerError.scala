package babymed.exception

sealed trait CustomerError extends BabyMedError

object CustomerError {
  final case class CustomerPhoneInUse(cause: String) extends CustomerError
}
