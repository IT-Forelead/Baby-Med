package babymed.services.users.proto

import babymed.domain.ResponseData
import babymed.services.users.domain.CreatePatient
import babymed.services.users.domain.Patient
import babymed.services.users.domain.PatientFilters
import babymed.services.users.domain.PatientWithAddress
import babymed.services.users.domain.Region
import babymed.services.users.domain.Town
import babymed.services.users.domain.types.PatientId
import babymed.services.users.domain.types.RegionId
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec

@service(Custom)
trait Patients[F[_]] {
  def createPatient(createPatient: CreatePatient): F[Patient]
  def getPatientById(patientId: PatientId): F[Option[PatientWithAddress]]
  def getPatients(filters: PatientFilters): F[ResponseData[PatientWithAddress]]
  def getTotalPatients(filters: PatientFilters): F[Long]
  def getRegions: F[List[Region]]
  def getTownsByRegionId(regionId: RegionId): F[List[Town]]
}

object Patients {}
