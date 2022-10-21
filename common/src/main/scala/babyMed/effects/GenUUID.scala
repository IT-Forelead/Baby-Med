package babyMed.effects

import java.util.UUID

import cats.ApplicativeThrow
import cats.effect.Sync

trait GenUUID[F[_]] {
  def make: F[UUID]
  def read(str: String): F[UUID]
}

object GenUUID {
  def apply[F[_]](implicit ev: GenUUID[F]): GenUUID[F] = ev

  implicit def syncGenUUID[F[_]: Sync]: GenUUID[F] = new GenUUID[F] {
    override def make: F[UUID] = Sync[F].delay(UUID.randomUUID())
    override def read(str: String): F[UUID] =
      ApplicativeThrow[F].catchNonFatal(UUID.fromString(str))
  }
}
