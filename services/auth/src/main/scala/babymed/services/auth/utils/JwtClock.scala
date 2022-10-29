package babymed.services.auth.utils

import cats.effect.Sync

import java.time.Clock

trait JwtClock[F[_]] {
  def utc: F[Clock]
}

object JwtClock {
  def apply[F[_]: JwtClock]: JwtClock[F] = implicitly

  implicit def forSync[F[_]: Sync]: JwtClock[F] =
    new JwtClock[F] {
      def utc: F[Clock] = Sync[F].delay(Clock.systemUTC())
    }
}
