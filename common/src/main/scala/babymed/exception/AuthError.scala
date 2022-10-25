package babymed.exception

sealed trait AuthError extends BabyMedError

object AuthError {
  final case class NoSuchUser(cause: String) extends AuthError
  final case class RoleDoesNotMatch(cause: String) extends AuthError
  final case class PasswordExpired(cause: String, firstLogin: Boolean, userId: Int) extends AuthError
  final case class PasswordDoesNotMatch(cause: String) extends AuthError
  final case class NoSuchReferer(cause: String) extends AuthError
}
