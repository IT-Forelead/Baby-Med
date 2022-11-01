package babymed.services.users;

import babymed.services.users.boundary.Users
import babymed.support.services.rpc.GrpcServer
import babymed.support.services.rpc.GrpcServerConfig
import cats.effect.Async
import cats.effect.ExitCode
import cats.effect.Resource
import io.grpc.ServerServiceDefinition

case class GrpcModule(services: List[ServerServiceDefinition]) {
  def startServer[F[_]: Async](config: GrpcServerConfig): F[ExitCode] =
    GrpcServer.start[F](config, services)
}

object GrpcModule {
  def make[F[_]: Async](
      users: Users[F]
    ): Resource[F, GrpcModule] =
    proto
      .Users
      .bindService[F](users)
      .map(serverDef => GrpcModule.apply(List(serverDef)))
}
