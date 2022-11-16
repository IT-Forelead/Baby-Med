package babymed.services.babymed.api.setup

import cats.effect.Async
import cats.effect.Resource

import babymed.services.babymed.api.Services
import babymed.services.users.proto._
import babymed.services.visits.proto
import babymed.services.visits.proto.Visits

case class RpcClients[F[_]](
    users: Users[F],
    patients: Patients[F],
    services: proto.Services[F],
    visits: Visits[F],
  ) {
  val toServer: Services[F] = Services[F](users, patients, services, visits)
}

object RpcClients {
  def make[F[_]: Async](config: Config.ServicesConfig): Resource[F, RpcClients[F]] =
    for {
      userClient <- Users.client[F](config.users.channelAddress)
      patientClient <- Patients.client[F](config.users.channelAddress)
      serviceClient <- proto.Services.client[F](config.users.channelAddress)
      visitClient <- Visits.client[F](config.visits.channelAddress)
    } yield RpcClients[F](userClient, patientClient, serviceClient, visitClient)
}
