package babymed.syntax

import scala.language.implicitConversions

import io.circe.Encoder
import io.circe.Printer
import io.circe.syntax.EncoderOps

trait GenericSyntax {
  implicit def genericSyntaxGenericTypeOps[A](obj: A): GenericTypeOps[A] =
    new GenericTypeOps[A](obj)
}

final class GenericTypeOps[A](private val obj: A) {
  private val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  def toOptWhen(cond: => Boolean): Option[A] = if (cond) Some(obj) else None

  def toJson(implicit encoder: Encoder[A]): String = obj.asJson.printWith(printer)
}
