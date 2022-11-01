package babymed.util

import scala.annotation.implicitNotFound

import ciris.ConfigDecoder
import derevo.Derivation
import derevo.NewTypeDerivation

object Decoder {
  type Id[A] = ConfigDecoder[String, A]
}

object configDecoder extends Derivation[Decoder.Id] with NewTypeDerivation[Decoder.Id] {
  def instance(implicit ev: OnlyNewtypes): Nothing = ev.absurd

  @implicitNotFound("Only newtypes instances can be derived")
  final abstract class OnlyNewtypes {
    def absurd: Nothing = ???
  }
}
