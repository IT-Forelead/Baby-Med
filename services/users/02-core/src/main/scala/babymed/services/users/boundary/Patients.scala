package babymed.services.users.boundary

import cats.Monad
import cats.implicits._
import babymed.domain.ResponseData
import babymed.services.users.domain.{City, CreatePatient, Patient, PatientFilters, PatientWithAddress, PatientWithName, Region}
import babymed.services.users.domain.types.{Fullname, PatientId, RegionId}
import babymed.services.users.proto
import babymed.services.users.repositories.PatientsRepository

class Patients[F[_]: Monad](patientsRepository: PatientsRepository[F]) extends proto.Patients[F] {
  override def create(createPatient: CreatePatient): F[Patient] =
    patientsRepository.create(createPatient)
  override def getPatientById(patientId: PatientId): F[Option[PatientWithAddress]] =
    patientsRepository.getPatientById(patientId)
  override def getPatients(filters: PatientFilters): F[ResponseData[PatientWithAddress]] =
    for {
      customers <- patientsRepository.get(filters)
      total <- patientsRepository.getTotal(filters)
    } yield ResponseData(customers, total)
  override def getTotalPatients(filters: PatientFilters): F[Long] =
    patientsRepository.getTotal(filters)
  override def getRegions: F[List[Region]] =
    patientsRepository.getRegions
  override def getCitiesByRegionId(regionId: RegionId): F[List[City]] =
    patientsRepository.getCitiesByRegionId(regionId)
  override def getPatientsByName(name: Fullname): F[List[PatientWithName]] =
    patientsRepository.getPatientsByName(name)
}
