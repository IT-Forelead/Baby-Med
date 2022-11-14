package babymed.services.users.boundary

import cats.Monad
import cats.implicits._
import babymed.domain.ResponseData
import babymed.services.users.domain.{CreatePatient, Patient, PatientFilters, PatientWithAddress, Region, Town}
import babymed.services.users.domain.types.{PatientId, RegionId}
import babymed.services.users.proto
import babymed.services.users.repositories.PatientsRepository

class Patients[F[_]: Monad](patientsRepository: PatientsRepository[F])
    extends proto.Patients[F] {
  override def createPatient(createPatient: CreatePatient): F[Patient] =
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
  override def getTownsByRegionId(regionId: RegionId): F[List[Town]] =
    patientsRepository.getTownsByRegionId(regionId)
}
