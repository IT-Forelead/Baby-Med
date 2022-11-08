package babymed.effects

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

import cats.effect._

import babymed.syntax.javaTime._

trait Calendar[F[_]] {
  def currentDate: F[LocalDate]
  def currentDateTime: F[LocalDateTime]
  def currentInstant: F[Instant]
}

object Calendar {
  def apply[F[_]](implicit C: Calendar[F]): Calendar[F] = C

  implicit def calendarForSync[F[_]: Sync]: Calendar[F] =
    new Calendar[F] {
      override def currentDate: F[LocalDate] =
        Sync[F].delay(LocalDate.now)

      override def currentDateTime: F[LocalDateTime] =
        Sync[F].delay(LocalDateTime.now.noNanos)

      override def currentInstant: F[Instant] =
        Sync[F].delay(Instant.now.noNanos)
    }
}
