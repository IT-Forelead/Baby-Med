package babymed.support.services.file

import java.io.InputStream

import scala.io.Codec
import scala.io.Source

import cats.effect.Sync
import cats.implicits._
import fs2._
import fs2.io.file.Files
import fs2.io.file.Path

trait FileLoader[F[_]] {
  def inputStreamAsString(is: InputStream): F[String]
  def resourceAsInputStream(path: String): F[InputStream]
  def resourceAsF2Stream(path: String): Stream[F, Byte]
  def resourceAsString(path: String): F[String]
  def getResourceString(path: String): Stream[F, String]
  def decodeToString: Pipe[F, Byte, String]
}

object FileLoader {
  def apply[F[_]](implicit ev: FileLoader[F]): FileLoader[F] = ev

  implicit def fileLoader[F[_]: Files](implicit F: Sync[F]): FileLoader[F] =
    new FileLoader[F] {
      implicit val codec: Codec = Codec.UTF8

      override def inputStreamAsString(is: InputStream): F[String] =
        for {
          source <- F.delay(Source.fromInputStream(is))
          lines = source.mkString
          _ <- F.delay(source.close())
        } yield lines

      override def resourceAsInputStream(path: String): F[InputStream] =
        F.delay(getClass.getResourceAsStream(path))

      override def resourceAsString(path: String): F[String] =
        for {
          source <- F.delay(Source.fromResource(path))
          lines = source.mkString
          _ <- F.delay(source.close())
        } yield lines

      override def resourceAsF2Stream(path: String): Stream[F, Byte] =
        Files[F].readAll(Path(path))

      override def decodeToString: Pipe[F, Byte, String] =
        _.through(text.utf8.decode)
          .through(text.lines)

      override def getResourceString(path: String): Stream[F, String] =
        resourceAsF2Stream(path).through(decodeToString)
    }
}
