package babymed.support.services.rpc

import cats.effect.Async
import cats.effect.ExitCode
import cats.implicits._
import eu.timepit.refined.auto.autoUnwrap
import higherkindness.mu.rpc.server.AddService
import higherkindness.mu.rpc.server.netty.MaxMessageSize
import higherkindness.mu.rpc.server.{ GrpcServer => MuGrpcServer }
import io.grpc.ServerServiceDefinition

object GrpcServer {
  def start[F[_]: Async](
      config: GrpcServerConfig,
      serviceDefinitions: List[ServerServiceDefinition],
    ): F[ExitCode] =
    MuGrpcServer
      .netty[F](
        config.grpcPort,
        serviceDefinitions.map(AddService) ::: List(MaxMessageSize(Int.MaxValue)),
      )
      .flatMap(s => MuGrpcServer.server[F](s).as(ExitCode.Success))
}
