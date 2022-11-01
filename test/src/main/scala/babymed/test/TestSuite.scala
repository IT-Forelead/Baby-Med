package babymed.test

import cats.effect.IO
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.noop.NoOpLogger
import weaver.SimpleIOSuite
import weaver.scalacheck.Checkers

trait TestSuite extends SimpleIOSuite with Checkers {
  implicit val logger: SelfAwareStructuredLogger[IO] = NoOpLogger[IO]
}
