package babymed.domain

import cats.Show
import io.circe.Decoder
import io.circe.Encoder

sealed trait AppMode {
  lazy val value: String = this.toString
}

object AppMode {
  case object TEST extends AppMode
  case object DEV extends AppMode
  case object PROD extends AppMode

  val all: List[AppMode] = List(TEST, DEV, PROD)

  def find(value: String): Option[AppMode] =
    all.find(_.value.equalsIgnoreCase(value))

  def unsafeFrom(value: String): AppMode =
    find(value).getOrElse(throw new IllegalArgumentException(s"value doesn't match [ $value ]"))

  implicit val enc: Encoder[AppMode] = Encoder.encodeString.contramap[AppMode](_.value)
  implicit val dec: Decoder[AppMode] = Decoder.decodeString.map(unsafeFrom)
  implicit val show: Show[AppMode] = Show.show(_.value)
}
