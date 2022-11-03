package babymed.services.babymed.api.setup

import cats.effect.Concurrent
import cats.effect.Resource
import cats.effect.std.Console
import cats.implicits.toFlatMapOps
import cats.implicits.toFoldableOps
import babymed.support.redis.RedisConfig
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.RedisCommands
import dev.profunktor.redis4cats.effect.MkRedis
import fs2.io.net.Network
import org.typelevel.log4cats.Logger

case class ServiceResources[F[_]](
    redis: RedisCommands[F, String, String]
  )

object ServiceResources {
  private[this] def checkRedisConnection[F[_]: Concurrent: Logger](
      redis: RedisCommands[F, String, String]
    ): F[Unit] =
    redis.info.flatMap {
      _.get("redis_version").traverse_ { v =>
        Logger[F].info(s"Connected to Redis $v")
      }
    }

  private[this] def redisResource[F[_]: Concurrent: Logger: MkRedis](
      config: RedisConfig
    ): Resource[F, RedisCommands[F, String, String]] =
    Redis[F].utf8(config.uri.value).evalTap(checkRedisConnection[F])

  def make[F[_]: Concurrent: Console: Logger: Network: MkRedis](
      config: Config
    ): Resource[F, ServiceResources[F]] =
    redisResource(config.redis).map(ServiceResources[F])
}
