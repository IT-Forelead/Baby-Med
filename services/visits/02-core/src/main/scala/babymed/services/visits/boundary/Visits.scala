package babymed.services.visits.boundary

import cats.Monad

import babymed.services.visits.proto
import babymed.services.visits.repositories.VisitsRepository

class Visits[F[_]: Monad](visitsRepository: VisitsRepository[F]) extends proto.Visits[F] {}
