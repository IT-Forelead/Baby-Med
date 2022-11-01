package babymed.services.payments

import cats.effect.Async
import cats.effect.ExitCode
import cats.effect.Resource
import io.grpc.ServerServiceDefinition

import babymed.services.payments.boundary.Payments
import babymed.support.services.rpc.GrpcServer
import babymed.support.services.rpc.GrpcServerConfig

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
