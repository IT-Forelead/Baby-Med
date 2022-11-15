package babymed.services.visits.generators

import org.scalacheck.Gen

import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.EditService
import babymed.services.visits.domain.Service
import babymed.services.visits.domain.types.ServiceId

trait ServiceGenerators extends TypeGen {
  lazy val serviceGen: Gen[Service] =
    for {
      id <- serviceIdGen
      name <- serviceNameGen
      cost <- costGen
    } yield Service(id, name, cost)

  lazy val createServiceGen: Gen[CreateService] =
    for {
      name <- serviceNameGen
      cost <- costGen
    } yield CreateService(name, cost)

  def editServiceGen(serviceId: Option[ServiceId] = None): Gen[EditService] =
    for {
      id <- serviceIdGen
      name <- serviceNameGen
      cost <- costGen
    } yield EditService(id = serviceId.getOrElse(id), name, cost)
}
