package babymed.services.users.setup

import cats.effect.Async
import cats.effect.Resource

import babymed.services.messages.proto.Messages
import babymed.services.users.Services

case class RpcClients[F[_]](
    messages: Messages[F]
  ) {
  val toServer: Services[F] = Services[F](messages)
}

object RpcClients {
  def make[F[_]: Async](config: Config.ServicesConfig): Resource[F, RpcClients[F]] =
    for {
      messageClient <- Messages.client[F](config.messages.channelAddress)
    } yield RpcClients[F](messageClient)
}
