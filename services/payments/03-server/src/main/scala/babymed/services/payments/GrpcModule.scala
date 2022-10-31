package babymed.services.payments

import babymed.services.payments.boundary.Payments
import cats.effect.Async
import cats.effect.ExitCode
import cats.effect.Resource
import babymed.support.services.rpc.GrpcServer
import babymed.support.services.rpc.GrpcServerConfig
import io.grpc.ServerServiceDefinition

case class GrpcModule(services: List[ServerServiceDefinition]) {
  def startServer[F[_]: Async](config: GrpcServerConfig): F[ExitCode] =
    GrpcServer.start[F](config, services)
}

object GrpcModule {
  def make[F[_]: Async](
      payments: Payments[F]
    ): Resource[F, GrpcModule] =
    proto
      .Payments
      .bindService[F](payments)
      .map(serverDef => GrpcModule.apply(List(serverDef)))
}
