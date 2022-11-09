package babymed.util

import scala.util.Try

class MyPathVar[A](cast: String => Try[A]) {
  def unapply(str: String): Option[A] =
    if (str.nonEmpty)
      cast(str).toOption
    else
      None
}
