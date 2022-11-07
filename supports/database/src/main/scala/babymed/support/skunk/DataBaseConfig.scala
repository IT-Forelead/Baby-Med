package babymed.support.skunk

import cats.implicits.catsSyntaxTuple6Parallel
import ciris._
import ciris.refined.refTypeConfigDecoder
import eu.timepit.refined.cats.refTypeShow
import eu.timepit.refined.types.net.NonSystemPortNumber
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString

case class DataBaseConfig(
    host: NonEmptyString,
    port: NonSystemPortNumber,
    user: NonEmptyString,
    password: Secret[NonEmptyString],
    database: NonEmptyString,
    poolSize: PosInt,
  )

object DataBaseConfig {
  def configValues: ConfigValue[Effect, DataBaseConfig] = (
    env("POSTGRES_HOST").as[NonEmptyString],
    env("POSTGRES_PORT").as[NonSystemPortNumber],
    env("POSTGRES_USER").as[NonEmptyString],
    env("POSTGRES_PASSWORD").as[NonEmptyString].secret,
    env("POSTGRES_DATABASE").as[NonEmptyString],
    env("POSTGRES_POOL_SIZE").as[PosInt],
  ).parMapN(DataBaseConfig.apply)
}
