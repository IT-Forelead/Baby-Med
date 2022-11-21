package babymed.services.users.setup

import babymed.support.database.MigrationsConfig
import babymed.support.services.http4s.HttpServerConfig
import babymed.support.services.rpc.GrpcServerConfig
import babymed.support.skunk.DataBaseConfig
import babymed.syntax.refined.commonSyntaxAutoUnwrapV

case class Config(
    rpcServer: GrpcServerConfig,
    httpServer: HttpServerConfig,
    database: DataBaseConfig,
  ) {
  lazy val migrations: MigrationsConfig = MigrationsConfig(
    hostname = database.host,
    port = database.port,
    database = database.database,
    username = database.user,
    password = database.password.value,
    schema = "public",
    location = "db/migration",
  )
}
