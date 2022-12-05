package babymed.services.visits.proto

import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.EditService
import babymed.services.visits.domain.Service
import babymed.services.visits.domain.ServiceType
import babymed.services.visits.domain.ServiceWithTypeName
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.domain.types.ServiceTypeId
import babymed.services.visits.domain.types.ServiceTypeName
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec

@service(Custom)
trait Services[F[_]] {
  def create(createService: CreateService): F[Service]
  def get: F[List[ServiceWithTypeName]]
  def getServicesByTypeId(serviceTypeId: ServiceTypeId): F[List[Service]]
  def edit(editService: EditService): F[Unit]
  def delete(serviceId: ServiceId): F[Unit]
  def createServiceType(name: ServiceTypeName): F[ServiceType]
  def getServiceTypes: F[List[ServiceType]]
  def deleteServiceType(id: ServiceTypeId): F[Unit]
}

object Services {}
