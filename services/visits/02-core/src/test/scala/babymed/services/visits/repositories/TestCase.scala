package babymed.services.visits.repositories

import cats.effect.IO
import weaver.Expectations

trait TestCase[Res] {
  def check(implicit dao: Res): IO[Expectations]
}
