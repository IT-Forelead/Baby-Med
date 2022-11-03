package babymed.services.babymed.api.setup

import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import babymed.services.auth.impl.Security
import babymed.services.babymed.api.ServerEnvironment
import babymed.support.services.file.FileLoader
import dev.profunktor.redis4cats.log4cats._
import org.typelevel.log4cats.Logger

case class ServiceEnvironment[F[_]: Async: FileLoader](
    config: Config,
    security: Security[F],
    rpcClients: RpcClients[F],
    libraries: Libraries[F],
  ) {
  lazy val toServer: ServerEnvironment[F] =
    ServerEnvironment(
      config = config.toServer,
      security = security,
      services = rpcClients.toServer,
    )
}

object ServiceEnvironment {
  def make[F[_]: Console: Logger](implicit F: Async[F]): Resource[F, ServiceEnvironment[F]] =
    for {
      config <- Resource.eval(ConfigLoader.load[F])
      resource <- ServiceResources.make[F](config)
      services <- RpcClients.make[F](config.services)
      libraries = Libraries.make[F](resource.redis)
      security = Security.make[F](config.jwtConfig, services.users, libraries.redis)
    } yield ServiceEnvironment[F](config, security, services, libraries)
}
