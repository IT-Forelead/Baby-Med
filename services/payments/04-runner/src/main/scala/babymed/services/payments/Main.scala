package babymed.services.payments

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import cats.implicits.toTraverseOps
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import babymed.services.payments.setup.ServiceEnvironment

object Main extends IOApp {
  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  private def runnable: Resource[IO, List[IO[ExitCode]]] =
    for {
      env <- ServiceEnvironment.make[IO]
      rpcModule <- GrpcModule.make[IO](env.payments)
      httpModule <- HttpModule.make[IO](env.config.httpServer)
      rpcServer = rpcModule.startServer[IO](env.config.rpcServer)
    } yield List(rpcServer, httpModule)

  override def run(
      args: List[String]
    ): IO[ExitCode] =
    runnable.use { runners =>
      for {
        fibers <- runners.traverse(_.start)

        _ <- fibers.traverse(_.join)
      } yield ExitCode.Success
    }
}
