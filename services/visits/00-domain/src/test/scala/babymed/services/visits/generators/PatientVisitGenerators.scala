package babymed.services.visits.generators

import org.scalacheck.Gen

import babymed.services.users.domain.types.PatientId
import babymed.services.users.generators.PatientGenerators
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitInfo
import babymed.services.visits.domain.types.ServiceId

trait PatientVisitGenerators extends TypeGen with ServiceGenerators with PatientGenerators {
  lazy val patientVisitGen: Gen[PatientVisit] =
    for {
      id <- patientVisitIdGen
      createdAt <- localDateTimeGen
      patientId <- patientIdGen
      serviceId <- serviceIdGen
      payment_status <- paymentStatusGen
    } yield PatientVisit(id, createdAt, patientId, serviceId, payment_status)

  def createPatientVisitGen(
      maybePatientId: Option[PatientId] = None,
      maybeServiceId: Option[ServiceId] = None,
    ): Gen[CreatePatientVisit] =
    for {
      patientId <- patientIdGen
      serviceId <- serviceIdGen
    } yield CreatePatientVisit(
      maybePatientId.getOrElse(patientId),
      maybeServiceId.getOrElse(serviceId),
    )

  lazy val patientVisitInfoGen: Gen[PatientVisitInfo] =
    for {
      patientVisit <- patientVisitGen
      patient <- patientGen
      serviceWithTypeName <- serviceWithTypeNameGen
      region <- regionGen
      city <- cityGen
    } yield PatientVisitInfo(patientVisit, patient, serviceWithTypeName, region, city)
}
