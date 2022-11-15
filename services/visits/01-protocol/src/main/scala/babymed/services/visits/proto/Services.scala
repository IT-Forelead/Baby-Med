package babymed.services.visits.proto

import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.EditService
import babymed.services.visits.domain.Service
import babymed.services.visits.domain.types.ServiceId
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec

@service(Custom)
trait Services[F[_]] {
  def create(createService: CreateService): F[Service]
  def get: F[List[Service]]
  def edit(editService: EditService): F[Unit]
  def delete(serviceId: ServiceId): F[Unit]
}

object Services
