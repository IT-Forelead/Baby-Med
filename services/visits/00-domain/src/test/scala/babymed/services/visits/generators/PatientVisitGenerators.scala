package babymed.services.visits.generators

import org.scalacheck.Gen

import babymed.services.users.domain.types.PatientId
import babymed.services.users.domain.types.UserId
import babymed.services.users.generators.PatientGenerators
import babymed.services.users.generators.UserGenerators
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitInfo
import babymed.services.visits.domain.types.ServiceId

trait PatientVisitGenerators
    extends TypeGen
       with UserGenerators
       with ServiceGenerators
       with PatientGenerators {
  lazy val patientVisitGen: Gen[PatientVisit] =
    for {
      id <- patientVisitIdGen
      createdAt <- localDateTimeGen
      patientId <- patientIdGen
      userId <- userIdGen
      serviceId <- serviceIdGen
      payment_status <- paymentStatusGen
    } yield PatientVisit(id, createdAt, patientId, userId, serviceId, payment_status)

  def createPatientVisitGen(
      maybePatientId: Option[PatientId] = None,
      maybeUserId: Option[UserId] = None,
      maybeServiceId: Option[ServiceId] = None,
    ): Gen[CreatePatientVisit] =
    for {
      patientId <- patientIdGen
      userId <- userIdGen
      serviceId <- serviceIdGen
    } yield CreatePatientVisit(
      maybePatientId.getOrElse(patientId),
      maybeUserId.getOrElse(userId),
      maybeServiceId.getOrElse(serviceId),
    )

  lazy val patientVisitInfoGen: Gen[PatientVisitInfo] =
    for {
      patientVisit <- patientVisitGen
      patient <- patientGen
      user <- userGen
      service <- serviceGen
      region <- regionGen
      town <- townGen
    } yield PatientVisitInfo(patientVisit, patient, user, service, region, town)
}
