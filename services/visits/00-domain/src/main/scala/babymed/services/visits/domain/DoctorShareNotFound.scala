package babymed.services.visits.domain

import scala.util.control.NoStackTrace

import babymed.services.visits.domain.types.ServiceId

case class DoctorShareNotFound(serviceId: ServiceId) extends NoStackTrace
