package babymed.services.babymed.api.setup

import babymed.support.redis.RedisClient
import cats.effect.Async
import dev.profunktor.redis4cats.RedisCommands
import org.typelevel.log4cats.Logger

final case class Libraries[F[_]](
    redis: RedisClient[F]
  )

object Libraries {
  def make[F[_]: Async: Logger](
      redisCommands: RedisCommands[F, String, String]
    ): Libraries[F] =
    Libraries[F](
      RedisClient[F](redisCommands)
    )
}
