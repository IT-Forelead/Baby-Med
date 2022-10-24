package babymed.support.redis

import babymed.domain.refinements.UriAddress
import ciris._
import ciris.refined.refTypeConfigDecoder

case class RedisConfig(uri: UriAddress)
object RedisConfig {
  def configValues: ConfigValue[Effect, RedisConfig] =
    env("REDIS_SERVER_URI")
      .as[UriAddress]
      .map(RedisConfig.apply)
}
