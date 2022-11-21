package babymed.services.visits.repositories.sql

import skunk._
import skunk.implicits._

import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.EditService
import babymed.services.visits.domain.Service
import babymed.services.visits.domain.types.ServiceId

object ServicesSql {
  private val Columns = serviceId ~ serviceName ~ cost

  val encoder: Encoder[ServiceId ~ CreateService] = Columns.contramap {
    case id ~ cs =>
      id ~ cs.name ~ cs.cost
  }

  val decoder: Decoder[Service] = Columns.map {
    case id ~ name ~ cost =>
      Service(id, name, cost)
  }

  val insertSql: Query[ServiceId ~ CreateService, Service] =
    sql"""INSERT INTO services VALUES ($encoder) RETURNING id, name, cost"""
      .query(decoder)

  val selectSql: Query[Void, Service] =
    sql"""SELECT id, name, cost FROM services WHERE deleted = false ORDER BY name ASC"""
      .query(decoder)

  val updateSql: Command[EditService] =
    sql"""UPDATE services SET name = $serviceName, cost = $cost WHERE id = $serviceId"""
      .command
      .contramap { es: EditService =>
        es.name ~ es.cost ~ es.id
      }

  val deleteSql: Command[ServiceId] =
    sql"""UPDATE services SET deleted = true WHERE id = $serviceId""".command
}
