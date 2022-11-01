package babymed.exception

abstract class BabyMedError extends Throwable {
  def cause: String
  override def getMessage: String = cause
}
