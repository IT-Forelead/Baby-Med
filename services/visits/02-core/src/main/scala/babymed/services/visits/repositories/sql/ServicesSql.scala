package babymed.services.visits.repositories.sql

import skunk._
import skunk.implicits._

import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.Service
import babymed.services.visits.domain.types.ServiceId

object ServicesSql {
  val serviceId: Codec[ServiceId] = identity[ServiceId]

  private val Columns = serviceId ~ serviceName ~ cost

  val encoder: Encoder[ServiceId ~ CreateService] = Columns.contramap {
    case id ~ cs =>
      id ~ cs.name ~ cs.cost
  }

  val decoder: Decoder[Service] = Columns.map {
    case id ~ name ~ cost =>
      Service(id, name, cost)
  }

  val insert: Query[ServiceId ~ CreateService, Service] =
    sql"""INSERT INTO services VALUES ($encoder) RETURNING id, name, cost"""
      .query(decoder)

  val selectSql: Query[Void, Service] =
    sql"""SELECT id, name, cost FROM services WHERE deleted = false ORDER BY name ASC"""
      .query(decoder)
}
