package babymed.services.users.repositories

import java.time.LocalDateTime
import java.util.UUID

import cats.effect.IO
import cats.effect.Resource
import cats.implicits._
import org.scalacheck.Gen
import skunk.Session
import skunk.implicits._
import tsec.passwordhashers.jca.SCrypt

import babymed.services.users.domain.CreatePatient
import babymed.services.users.domain.CreateUser
import babymed.services.users.domain.types.CityId
import babymed.services.users.domain.types.PatientId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.SubRoleId
import babymed.services.users.domain.types.UserId
import babymed.services.users.generators.PatientGenerators
import babymed.services.users.generators.UserGenerators
import babymed.services.users.repositories.sql.PatientsSql
import babymed.services.users.repositories.sql.UsersSql
import babymed.support.skunk.syntax.all._
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.util.RandomGenerator

object data extends PatientGenerators with UserGenerators {
  implicit private def gen2instance[T](gen: Gen[T]): T = gen.sample.get

  object regions {
    val id1: RegionId = RegionId(UUID.fromString("ad514b71-3096-4be5-a455-d87abbb081b2"))
    val id2: RegionId = RegionId(UUID.fromString("3b316182-e55c-4e03-8811-052fcd888236"))
  }

  object cities {
    val id1: CityId = CityId(UUID.fromString("0d073b76-08ce-4b78-a88c-a0cb6f80eaf9"))
    val id2: CityId = CityId(UUID.fromString("b272f8fe-e0a1-4157-903f-91d1b22b6770"))
  }

  object subRoles {
    val id1: SubRoleId = SubRoleId(UUID.fromString("c64c197f-b3d5-47c4-b522-b8a776e51aea"))
    val id2: SubRoleId = SubRoleId(UUID.fromString("49b6e573-00e9-4bb6-914a-0d1c9649a8b1"))
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
    val data1: CreateUser = createUserGen(subRoles.id2.some).get
    val data2: CreateUser = createUserGen(subRoles.id1.some).get
    val data3: CreateUser = createUserGen(subRoles.id1.some).get
    val values: Map[UserId, CreateUser] = Map(id1 -> data1, id2 -> data2, id3 -> data3)
  }

  def setup(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    setupCustomers *> setupUsers

  private def setupCustomers(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
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
}
