package babymed.services.babymed.api.setup

import cats.effect.Async
import cats.effect.Resource

import babymed.services.babymed.api.Services
import babymed.services.payments.proto.Payments
import babymed.services.users.proto._

case class RpcClients[F[_]](
    users: Users[F],
    customers: Customers[F],
    payments: Payments[F],
  ) {
  val toServer: Services[F] = Services[F](users, customers, payments)
}

object RpcClients {
  def make[F[_]: Async](config: Config.ServicesConfig): Resource[F, RpcClients[F]] =
    for {
      userClient <- Users.client[F](config.users.channelAddress)
      customerClient <- Customers.client[F](config.users.channelAddress)
      paymentClient <- Payments.client[F](config.payment.channelAddress)
    } yield RpcClients[F](userClient, customerClient, paymentClient)
}
