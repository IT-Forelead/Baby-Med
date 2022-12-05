package babymed.services.visits.repositories.sql

import skunk._
import skunk.codec.all.bool
import skunk.implicits._

import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.EditService
import babymed.services.visits.domain.Service
import babymed.services.visits.domain.ServiceType
import babymed.services.visits.domain.ServiceWithTypeName
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.domain.types.ServiceTypeId
import babymed.services.visits.domain.types.ServiceTypeName

object ServicesSql {
  private val Columns = serviceId ~ serviceTypeId ~ serviceName ~ price ~ bool
  private val ColumnsServiceType = serviceTypeId ~ serviceTypeName ~ bool

  val encoder: Encoder[ServiceId ~ CreateService] = Columns.contramap {
    case id ~ cs =>
      id ~ cs.serviceTypeId ~ cs.name ~ cs.price ~ false
  }

  val encServiceType: Encoder[ServiceTypeId ~ ServiceTypeName] = ColumnsServiceType.contramap {
    case id ~ name =>
      id ~ name ~ false
  }

  val decoder: Decoder[Service] = Columns.map {
    case id ~ serviceTypeId ~ name ~ price ~ _ =>
      Service(id, serviceTypeId, name, price)
  }

  val decServiceType: Decoder[ServiceType] = ColumnsServiceType.map {
    case id ~ name ~ _ =>
      ServiceType(id, name)
  }

  val decServiceWithTypeName: Decoder[ServiceWithTypeName] = (Columns ~ serviceTypeName).map {
    case id ~ serviceTypeId ~ name ~ price ~ _ ~ serviceTypeName =>
      ServiceWithTypeName(id, serviceTypeId, name, price, serviceTypeName)
  }

  val insertSql: Query[ServiceId ~ CreateService, Service] =
    sql"""INSERT INTO services VALUES ($encoder) RETURNING *""".query(decoder)

  val insertServiceTypeSql: Query[ServiceTypeId ~ ServiceTypeName, ServiceType] =
    sql"""INSERT INTO service_types VALUES ($encServiceType) RETURNING *""".query(decServiceType)

  val selectSql: Query[ServiceTypeId, Service] =
    sql"""SELECT * FROM services WHERE service_type_id = $serviceTypeId AND deleted = false ORDER BY name ASC"""
      .query(decoder)

  val selectServiceTypesSql: Query[Void, ServiceType] =
    sql"""SELECT * FROM service_types WHERE deleted = false ORDER BY name ASC"""
      .query(decServiceType)

  val selectServicesSql: Query[Void, ServiceWithTypeName] =
    sql"""SELECT services.*, service_types.name FROM services
        INNER JOIN service_types ON services.service_type_id = service_types.id
        WHERE services.deleted = false"""
      .query(decServiceWithTypeName)

  val deleteServiceTypeSql: Command[ServiceTypeId] =
    sql"""UPDATE service_types SET deleted = true WHERE id = $serviceTypeId""".command

  val updateSql: Command[EditService] =
    sql"""UPDATE services SET name = $serviceName, price = $price WHERE id = $serviceId"""
      .command
      .contramap { es: EditService =>
        es.name ~ es.price ~ es.id
      }

  val deleteSql: Command[ServiceId] =
    sql"""UPDATE services SET deleted = true WHERE id = $serviceId""".command
}
