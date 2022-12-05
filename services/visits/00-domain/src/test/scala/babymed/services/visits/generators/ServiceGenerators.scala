package babymed.services.visits.generators

import org.scalacheck.Gen

import babymed.services.visits.domain._
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.domain.types.ServiceTypeId

trait ServiceGenerators extends TypeGen {
  lazy val serviceGen: Gen[Service] =
    for {
      id <- serviceIdGen
      serviceTypeId <- serviceTypeIdGen
      name <- serviceNameGen
      price <- priceGen
    } yield Service(id, serviceTypeId, name, price)

  lazy val serviceWithTypeNameGen: Gen[ServiceWithTypeName] =
    for {
      id <- serviceIdGen
      serviceTypeId <- serviceTypeIdGen
      name <- serviceNameGen
      price <- priceGen
      serviceTypeName <- serviceTypeNameGen
    } yield ServiceWithTypeName(id, serviceTypeId, name, price, serviceTypeName)

  lazy val serviceTypeGen: Gen[ServiceType] =
    for {
      id <- serviceTypeIdGen
      name <- serviceTypeNameGen
    } yield ServiceType(id, name)

  def createServiceGen(
      maybeServiceTypeId: Option[ServiceTypeId] = None
    ): Gen[CreateService] =
    for {
      serviceTypeId <- serviceTypeIdGen
      name <- serviceNameGen
      price <- priceGen
    } yield CreateService(maybeServiceTypeId.getOrElse(serviceTypeId), name, price)

  def editServiceGen(serviceId: Option[ServiceId] = None): Gen[EditService] =
    for {
      id <- serviceIdGen
      serviceTypeId <- serviceTypeIdGen
      name <- serviceNameGen
      price <- priceGen
    } yield EditService(id = serviceId.getOrElse(id), serviceTypeId, name, price)
}
