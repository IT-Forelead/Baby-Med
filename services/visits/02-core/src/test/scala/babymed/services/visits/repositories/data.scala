package babymed.services.visits.repositories

import java.time.LocalDateTime
import java.util.UUID

import cats.effect.IO
import cats.effect.Resource
import cats.implicits.catsSyntaxOptionId
import cats.implicits.toFoldableOps
import org.scalacheck.Gen
import skunk.Session
import skunk.implicits.toIdOps
import tsec.passwordhashers.jca.SCrypt

import babymed.domain.Role.Doctor
import babymed.services.users.domain.CreatePatient
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.types.CityId
import babymed.services.users.domain.types.PatientId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.UserId
import babymed.services.users.generators.PatientGenerators
import babymed.services.users.generators.UserGenerators
import babymed.services.users.repositories.sql.PatientsSql
import babymed.services.users.repositories.sql.UsersSql
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.generators.PatientVisitGenerators
import babymed.services.visits.repositories.sql.ServicesSql
import babymed.services.visits.repositories.sql.VisitsSql
import babymed.support.skunk.syntax.all.skunkSyntaxQueryOps
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.util.RandomGenerator

object data extends PatientVisitGenerators with UserGenerators with PatientGenerators {
  implicit private def gen2instance[T](gen: Gen[T]): T = gen.sample.get

  object regions {
    val id1: RegionId = RegionId(UUID.fromString("ad514b71-3096-4be5-a455-d87abbb081b2"))
    val id2: RegionId = RegionId(UUID.fromString("3b316182-e55c-4e03-8811-052fcd888236"))
  }

  object cities {
    val id1: CityId = CityId(UUID.fromString("0d073b76-08ce-4b78-a88c-a0cb6f80eaf9"))
    val id2: CityId = CityId(UUID.fromString("b272f8fe-e0a1-4157-903f-91d1b22b6770"))
  }

  object patient {
    val id1: PatientId = patientIdGen.get
    val id2: PatientId = patientIdGen.get
    val id3: PatientId = patientIdGen.get
    val data1: CreatePatient = createPatientGen(regions.id2.some, cities.id2.some)
    val data2: CreatePatient = createPatientGen(regions.id2.some, cities.id2.some)
    val data3: CreatePatient = createPatientGen(regions.id2.some, cities.id2.some)
    val values: Map[PatientId, CreatePatient] = Map(id1 -> data1, id2 -> data2, id3 -> data3)
  }

  object user {
    val id1: UserId = userIdGen.get
    val id2: UserId = userIdGen.get
    val id3: UserId = userIdGen.get
    val data1: CreateUser = createUserGen().get.copy(role = Doctor)
    val data2: CreateUser = createUserGen().get.copy(role = Doctor)
    val data3: CreateUser = createUserGen().get.copy(role = Doctor)
    val values: Map[UserId, CreateUser] = Map(id1 -> data1, id2 -> data2, id3 -> data3)
  }

  object service {
    val id1: ServiceId = serviceIdGen.get
    val id2: ServiceId = serviceIdGen.get
    val id3: ServiceId = serviceIdGen.get
    val data1: CreateService = createServiceGen.get
    val data2: CreateService = createServiceGen.get
    val data3: CreateService = createServiceGen.get
    val values: Map[ServiceId, CreateService] = Map(id1 -> data1, id2 -> data2, id3 -> data3)
  }

  object visits {
    val id1: PatientVisitId = patientVisitIdGen.get
    val id2: PatientVisitId = patientVisitIdGen.get
    val id3: PatientVisitId = patientVisitIdGen.get
    val data1: CreatePatientVisit =
      createPatientVisitGen(data.patient.id1.some, data.user.id1.some, data.service.id1.some).get
    val data2: CreatePatientVisit =
      createPatientVisitGen(data.patient.id2.some, data.user.id2.some, data.service.id2.some).get
    val data3: CreatePatientVisit =
      createPatientVisitGen(data.patient.id3.some, data.user.id3.some, data.service.id3.some).get
    val values: Map[PatientVisitId, CreatePatientVisit] =
      Map(id1 -> data1, id2 -> data2, id3 -> data3)
  }

  def setup(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    setupPatients *> setupUsers *> setupServices *> setupVisits

  private def setupPatients(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    patient.values.toList.traverse_ {
      case id -> data =>
        PatientsSql.insert.queryUnique(id ~ LocalDateTime.now() ~ data)
    }

  private def setupUsers(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    user.values.toList.traverse_ {
      case id -> data =>
        UsersSql
          .insert
          .queryUnique(
            id ~ LocalDateTime.now() ~ data ~ SCrypt.hashpwUnsafe(RandomGenerator.randomPassword(6))
          )
    }

  private def setupServices(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    service.values.toList.traverse_ {
      case id -> data =>
        ServicesSql.insertSql.queryUnique(id ~ data)
    }

  private def setupVisits(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    visits.values.toList.traverse_ {
      case id -> data =>
        VisitsSql.insert.queryUnique(id ~ LocalDateTime.now() ~ data)
    }
}
