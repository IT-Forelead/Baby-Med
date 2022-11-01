package babymed.services.users.setup

import cats.effect.Concurrent
import cats.effect.MonadCancel
import cats.effect.Resource
import cats.effect.std.Console
import cats.implicits.toFlatMapOps
import eu.timepit.refined.auto.autoUnwrap
import fs2.io.net.Network
import natchez.Trace.Implicits.noop
import org.typelevel.log4cats.Logger
import skunk.Session
import skunk.SessionPool
import skunk.codec.all.text
import skunk.implicits.toStringOps
import skunk.util.Typer

import babymed.support.skunk.DataBaseConfig

case class ServiceResources[F[_]](
    postgres: Resource[F, Session[F]]
  )

object ServiceResources {
  private[this] def checkPostgresConnection[F[_]](
      postgres: Resource[F, Session[F]]
    )(implicit
      F: MonadCancel[F, Throwable],
      logger: Logger[F],
    ): F[Unit] =
    postgres.use { session =>
      session.unique(sql"select version();".query(text)).flatMap { v =>
        logger.info(s"Connected to Postgres $v")
      }
    }

  private[this] def postgresSqlResource[F[_]: Concurrent: Logger: Network: Console](
      config: DataBaseConfig
    ): SessionPool[F] =
    Session
      .pooled[F](
        host = config.host,
        port = config.port,
        user = config.user,
        password = Some(config.password.value),
        database = config.database,
        max = config.poolSize,
        strategy = Typer.Strategy.SearchPath,
      )
      .evalTap(checkPostgresConnection[F])

  def make[F[_]: Concurrent: Console: Logger: Network](
      config: Config
    ): Resource[F, ServiceResources[F]] =
    postgresSqlResource(config.database).map(ServiceResources[F])
}
