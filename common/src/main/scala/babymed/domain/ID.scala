package babymed.domain

import babymed.effects.GenUUID
import babymed.effects.IsUUID
import cats.Functor
import cats.implicits._

object ID {
  def make[F[_]: Functor: GenUUID, A: IsUUID]: F[A] =
    GenUUID[F].make.map(IsUUID[A].uuid.get)

  def read[F[_]: Functor: GenUUID, A: IsUUID](str: String): F[A] =
    GenUUID[F].read(str).map(IsUUID[A].uuid.get)
}
