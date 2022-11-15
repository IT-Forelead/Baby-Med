package babymed.services.visits.boundary

import cats.Monad

import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.domain.PatientVisitInfo
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.proto
import babymed.services.visits.repositories.VisitsRepository

class Visits[F[_]: Monad](visitsRepository: VisitsRepository[F]) extends proto.Visits[F] {
  override def create(createPatientVisit: CreatePatientVisit): F[PatientVisit] =
    visitsRepository.create(createPatientVisit)
  override def get(filters: PatientVisitFilters): F[List[PatientVisitInfo]] =
    visitsRepository.get(filters)
  override def getTotal(filters: PatientVisitFilters): F[Long] =
    visitsRepository.getTotal(filters)
  override def updatePaymentStatus(id: PatientVisitId): F[Unit] =
    visitsRepository.updatePaymentStatus(id)
}
