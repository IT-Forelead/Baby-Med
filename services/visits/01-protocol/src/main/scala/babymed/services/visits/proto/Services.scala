package babymed.services.visits.proto

import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.Service
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec

@service(Custom)
trait Services[F[_]] {
  def create(createService: CreateService): F[Service]
  def get: F[List[Service]]
}

object Services
