package babymed.services.users.repositories

import cats.effect.Concurrent
import cats.effect.MonadCancel
import cats.effect.Resource
import cats.implicits._
import skunk.Session
import skunk._
import skunk.codec.all.int8
import skunk.implicits.toIdOps

import babymed.domain.ID
import babymed.effects.Calendar
import babymed.effects.GenUUID
import babymed.exception.PatientError
import babymed.services.users.domain.City
import babymed.services.users.domain.CreatePatient
import babymed.services.users.domain.Patient
import babymed.services.users.domain.PatientFilters
import babymed.services.users.domain.PatientWithAddress
import babymed.services.users.domain.PatientWithName
import babymed.services.users.domain.Region
import babymed.services.users.domain.types.Fullname
import babymed.services.users.domain.types.PatientId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.repositories.sql.PatientsSql
import babymed.support.skunk.syntax.all._

trait PatientsRepository[F[_]] {
  def create(createPatient: CreatePatient): F[Patient]
  def getPatientById(patientId: PatientId): F[Option[PatientWithAddress]]
  def getPatientsByName(name: Fullname): F[List[PatientWithName]]
  def get(filters: PatientFilters): F[List[PatientWithAddress]]
  def getTotal(filters: PatientFilters): F[Long]
  def getRegions: F[List[Region]]
  def getCitiesByRegionId(regionId: RegionId): F[List[City]]
}

object PatientsRepository {
  def make[F[_]: GenUUID: Calendar: Concurrent](
      implicit
      session: Resource[F, Session[F]],
      F: MonadCancel[F, Throwable],
    ): PatientsRepository[F] = new PatientsRepository[F] {
    import sql.PatientsSql._

    override def create(createPatient: CreatePatient): F[Patient] =
      (for {
        id <- ID.make[F, PatientId]
        now <- Calendar[F].currentDateTime
        customer <- insert.queryUnique(id ~ now ~ createPatient)
      } yield customer).recoverWith {
        case SqlState.UniqueViolation(_) =>
          PatientError
            .CustomerPhoneInUse(createPatient.phone.value)
            .raiseError[F, Patient]
      }

    override def getPatientById(patientId: PatientId): F[Option[PatientWithAddress]] =
      selectById.queryOption(patientId)

    override def get(filters: PatientFilters): F[List[PatientWithAddress]] = {
      val query = PatientsSql.select(filters).paginateOpt(filters.limit, filters.page)
      query.fragment.query(PatientsSql.decPatientWithAddress).queryList(query.argument)
    }

    override def getTotal(filters: PatientFilters): F[Long] = {
      val query = PatientsSql.total(filters)
      query.fragment.query(int8).queryUnique(query.argument)
    }

    // The reason the name is sent twice is to search by first name and last name
    override def getPatientsByName(name: Fullname): F[List[PatientWithName]] =
      PatientsSql.getPatientsByName.queryList(name, name)

    override def getRegions: F[List[Region]] =
      PatientsSql.selectRegions.queryList(Void)

    override def getCitiesByRegionId(regionId: RegionId): F[List[City]] =
      PatientsSql.selectCitiesByRegionId.queryList(regionId)
  }
}
