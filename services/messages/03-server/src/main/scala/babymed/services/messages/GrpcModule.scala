package babymed.services.messages

import cats.effect.Async
import cats.effect.ExitCode
import cats.effect.Resource
import cats.implicits.toTraverseOps
import io.grpc.ServerServiceDefinition

import babymed.support.services.rpc.GrpcServer
import babymed.support.services.rpc.GrpcServerConfig

case class GrpcModule(services: List[ServerServiceDefinition]) {
  def startServer[F[_]: Async](config: GrpcServerConfig): F[ExitCode] =
    GrpcServer.start[F](config, services)
}

object GrpcModule {
  def make[F[_]: Async](
      env: ServerEnvironment[F]
    ): Resource[F, GrpcModule] =
    List(
      proto.Messages.bindService[F](env.services.messages)
    ).sequence.map(GrpcModule.apply)
}
