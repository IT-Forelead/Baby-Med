package babymed.test.generators

import cats.effect.Sync
import org.scalacheck.Gen
import org.scalacheck.Gen.option

trait GeneratorSyntax {
  implicit def genSyntax[T](generator: Gen[T]): GenSyntax[T] = new GenSyntax(generator)
}

class GenSyntax[T](generator: Gen[T]) {
  def sync[F[_]: Sync]: F[T] = Sync[F].delay(get)
  def get: T = generator.sample.get
  def getOpt: Option[T] = option(generator).sample.get
  def opt: Gen[Option[T]] = option(generator)
}
