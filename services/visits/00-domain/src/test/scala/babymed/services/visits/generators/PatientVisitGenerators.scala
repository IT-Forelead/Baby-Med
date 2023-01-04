package babymed.services.visits.generators

import org.scalacheck.Gen

import babymed.services.users.domain.types.PatientId
import babymed.services.users.domain.types.UserId
import babymed.services.users.generators.PatientGenerators
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitInfo
import babymed.services.visits.domain.PatientVisitReport
import babymed.services.visits.domain.types.ServiceId

trait PatientVisitGenerators extends TypeGen with ServiceGenerators with PatientGenerators {
  lazy val patientVisitGen: Gen[PatientVisit] =
    for {
      id <- patientVisitIdGen
      createdAt <- localDateTimeGen
      userId <- userIdGen
      patientId <- patientIdGen
      payment_status <- paymentStatusGen
    } yield PatientVisit(id, createdAt, userId, patientId, payment_status)

  def createPatientVisitGen(
      maybeUserId: Option[UserId] = None,
      maybePatientId: Option[PatientId] = None,
      maybeServiceId: Option[ServiceId] = None,
    ): Gen[CreatePatientVisit] =
    for {
      userId <- userIdGen
      patientId <- patientIdGen
      serviceId <- serviceIdGen
    } yield CreatePatientVisit(
      maybeUserId.getOrElse(userId),
      maybePatientId.getOrElse(patientId),
      List(maybeServiceId.getOrElse(serviceId)),
    )

  lazy val patientVisitInfoGen: Gen[PatientVisitInfo] =
    for {
      pv <- patientVisitGen
      fn <- firstNameGen
      ln <- lastNameGen
      patient <- patientGen
      region <- regionGen
      city <- cityGen
    } yield PatientVisitInfo(pv, fn, ln, patient, region, city)

  lazy val patientVisitReportGen: Gen[PatientVisitReport] =
    for {
      pv <- patientVisitGen
      fn <- firstNameGen
      ln <- lastNameGen
      patient <- patientGen
      services <- serviceWithTypeNameGen
      region <- regionGen
      city <- cityGen
    } yield PatientVisitReport(pv, fn, ln, patient, List(services), region, city)
}
