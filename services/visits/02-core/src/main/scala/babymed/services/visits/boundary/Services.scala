package babymed.services.visits.boundary

import cats.Monad

import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.EditService
import babymed.services.visits.domain.Service
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.proto
import babymed.services.visits.repositories.ServicesRepository

class Services[F[_]: Monad](servicesRepository: ServicesRepository[F]) extends proto.Services[F] {
  override def create(createService: CreateService): F[Service] =
    servicesRepository.create(createService)
  override def get: F[List[Service]] =
    servicesRepository.get
  override def edit(editService: EditService): F[Unit] =
    servicesRepository.edit(editService)
  override def delete(serviceId: ServiceId): F[Unit] =
    servicesRepository.delete(serviceId)
}
