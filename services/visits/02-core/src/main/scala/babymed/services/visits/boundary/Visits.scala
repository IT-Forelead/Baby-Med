package babymed.services.visits.boundary

import cats.Monad
import cats.effect.Sync
import cats.implicits._

import babymed.domain.ResponseData
import babymed.exception.UpdatePaymentStatusError
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.domain.PatientVisitInfo
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.proto
import babymed.services.visits.repositories.CheckupExpensesRepository
import babymed.services.visits.repositories.VisitsRepository

class Visits[F[_]: Monad: Sync](
    visitsRepository: VisitsRepository[F],
    checkupExpensesRepository: CheckupExpensesRepository[F],
  ) extends proto.Visits[F] {
  override def create(createPatientVisit: CreatePatientVisit): F[PatientVisit] =
    visitsRepository.create(createPatientVisit)
  override def get(filters: PatientVisitFilters): F[ResponseData[PatientVisitInfo]] =
    for {
      visits <- visitsRepository.get(filters)
      total <- visitsRepository.getTotal(filters)
    } yield ResponseData(visits, total)
  override def getTotal(filters: PatientVisitFilters): F[Long] =
    visitsRepository.getTotal(filters)
  override def updatePaymentStatus(id: PatientVisitId): F[PatientVisit] =
    for {
      update <- visitsRepository
        .updatePaymentStatus(id)
        .onError(error => UpdatePaymentStatusError(s"$error").raiseError[F, Unit])
      _ <- checkupExpensesRepository.create(update.serviceId)
    } yield update
}
