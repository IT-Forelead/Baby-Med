package babymed.services.users.boundary

import cats.Monad
import cats.implicits._

import babymed.domain.ResponseData
import babymed.services.users.domain.City
import babymed.services.users.domain.CreatePatient
import babymed.services.users.domain.Patient
import babymed.services.users.domain.PatientFilters
import babymed.services.users.domain.PatientWithAddress
import babymed.services.users.domain.Region
import babymed.services.users.domain.types.PatientId
import babymed.services.users.domain.types.RegionId
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
}
