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
import babymed.services.users.domain.types.SubRoleId
import babymed.services.users.domain.types.UserId
import babymed.services.users.generators._
import babymed.services.users.repositories.sql._
import babymed.services.visits.domain._
import babymed.services.visits.domain.types._
import babymed.services.visits.generators._
import babymed.services.visits.repositories.sql._
import babymed.support.skunk.syntax.all.skunkSyntaxCommandOps
import babymed.support.skunk.syntax.all.skunkSyntaxQueryOps
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.util.RandomGenerator

object data
    extends PatientVisitGenerators
       with UserGenerators
       with PatientGenerators
       with OperationExpenseGenerators
       with CheckupExpenseGenerators {
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
    val data1: CreateUser = createUserGen(subRoles.id2.some).get.copy(role = Doctor)
    val data2: CreateUser = createUserGen(subRoles.id2.some).get.copy(role = Doctor)
    val data3: CreateUser = createUserGen(subRoles.id2.some).get.copy(role = Doctor)
    val values: Map[UserId, CreateUser] = Map(id1 -> data1, id2 -> data2, id3 -> data3)
  }

  object serviceType {
    val id1: ServiceTypeId = serviceTypeIdGen.get
    val id2: ServiceTypeId = serviceTypeIdGen.get
    val id3: ServiceTypeId = serviceTypeIdGen.get
    val data1: ServiceTypeName = serviceTypeNameGen.get
    val data2: ServiceTypeName = serviceTypeNameGen.get
    val data3: ServiceTypeName = serviceTypeNameGen.get
    val values: Map[ServiceTypeId, ServiceTypeName] = Map(id1 -> data1, id2 -> data2, id3 -> data3)
  }

  object service {
    val id1: ServiceId = serviceIdGen.get
    val id2: ServiceId = serviceIdGen.get
    val id3: ServiceId = serviceIdGen.get
    val data1: CreateService = createServiceGen(data.serviceType.id1.some).get
    val data2: CreateService = createServiceGen(data.serviceType.id2.some).get
    val data3: CreateService = createServiceGen(data.serviceType.id3.some).get
    val values: Map[ServiceId, CreateService] = Map(id1 -> data1, id2 -> data2, id3 -> data3)
  }

  object visits {
    val id1: PatientVisitId = patientVisitIdGen.get
    val id2: PatientVisitId = patientVisitIdGen.get
    val id3: PatientVisitId = patientVisitIdGen.get
    val chequeId1: ChequeId = chequeIdGen.get
    val chequeId2: ChequeId = chequeIdGen.get
    val chequeId3: ChequeId = chequeIdGen.get
    val data1: InsertPatientVisit =
      InsertPatientVisit(
        id1,
        LocalDateTime.now(),
        data.user.id1,
        data.patient.id1,
        data.service.id1,
        chequeId1,
      )
    val data2: InsertPatientVisit =
      InsertPatientVisit(
        id2,
        LocalDateTime.now(),
        data.user.id2,
        data.patient.id2,
        data.service.id2,
        chequeId2,
      )
    val data3: InsertPatientVisit =
      InsertPatientVisit(
        id3,
        LocalDateTime.now(),
        data.user.id3,
        data.patient.id3,
        data.service.id3,
        chequeId3,
      )
    val values: List[InsertPatientVisit] = List(data1, data2, data3)
  }

  object operationExpenses {
    val id1: OperationExpenseId = operationExpenseIdGen.get
    val id2: OperationExpenseId = operationExpenseIdGen.get
    val id3: OperationExpenseId = operationExpenseIdGen.get
    val data1: CreateOperationExpense =
      createOperationExpenseGen(data.visits.id1.some).get
    val data2: CreateOperationExpense =
      createOperationExpenseGen(data.visits.id1.some).get
    val data3: CreateOperationExpense =
      createOperationExpenseGen(data.visits.id1.some).get
    val values: Map[OperationExpenseId, CreateOperationExpense] =
      Map(id1 -> data1, id2 -> data2, id3 -> data3)
  }

  object operationExpenseItems {
    val data1: OperationExpenseItem =
      OperationExpenseItem(
        operationExpenseId = data.operationExpenses.id1,
        userId = data.user.id1,
        subRoleId = data.subRoles.id1,
        price = priceGen.get,
      )
    val data2: OperationExpenseItem =
      OperationExpenseItem(
        operationExpenseId = data.operationExpenses.id2,
        userId = data.user.id2,
        subRoleId = data.subRoles.id2,
        price = priceGen.get,
      )
    val data3: OperationExpenseItem =
      OperationExpenseItem(
        operationExpenseId = data.operationExpenses.id3,
        userId = data.user.id3,
        subRoleId = data.subRoles.id2,
        price = priceGen.get,
      )
    val values: List[OperationExpenseItem] =
      List(data1, data2, data3)
  }

  object doctorShare {
    val id1: DoctorShareId = doctorShareIdGen.get
    val id2: DoctorShareId = doctorShareIdGen.get
    val id3: DoctorShareId = doctorShareIdGen.get
    val data1: CreateDoctorShare =
      createDoctorShareGen(data.service.id1.some, data.user.id1.some).get
    val data2: CreateDoctorShare =
      createDoctorShareGen(data.service.id2.some, data.user.id2.some).get
    val data3: CreateDoctorShare =
      createDoctorShareGen(data.service.id3.some, data.user.id3.some).get
    val values: Map[DoctorShareId, CreateDoctorShare] =
      Map(id1 -> data1, id2 -> data2, id3 -> data3)
  }

  object checkupExpense {
    val createData1: CreateCheckupExpense = CreateCheckupExpense(data.service.id1, data.visits.id1)
    val createData2: CreateCheckupExpense = CreateCheckupExpense(data.service.id2, data.visits.id2)
    val createData3: CreateCheckupExpense = CreateCheckupExpense(data.service.id3, data.visits.id3)
    val createCheckupExpense: List[CreateCheckupExpense] =
      List(createData1, createData2, createData3)
    val data1: CheckupExpense =
      CheckupExpense(
        id = checkupExpenseIdGen.get,
        createdAt = LocalDateTime.now(),
        doctorShareId = data.doctorShare.id1,
        patientVisitId = data.visits.id1,
        price = priceGen.get,
      )
    val data2: CheckupExpense =
      CheckupExpense(
        id = checkupExpenseIdGen.get,
        createdAt = LocalDateTime.now(),
        doctorShareId = data.doctorShare.id2,
        patientVisitId = data.visits.id2,
        price = priceGen.get,
      )
    val data3: CheckupExpense =
      CheckupExpense(
        id = checkupExpenseIdGen.get,
        createdAt = LocalDateTime.now(),
        doctorShareId = data.doctorShare.id3,
        patientVisitId = data.visits.id3,
        price = priceGen.get,
      )
    val values: List[CheckupExpense] =
      List(data1, data2, data3)
  }

  def setup(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    setupUsers *> setupPatients *> setupServiceTypes *> setupServices *>
      setupVisits *>
      setupOperationExpenses *> setupOperationExpenseItems *> setupDoctorShares *> setupCheckupExpenses

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

  private def setupServiceTypes(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    serviceType.values.toList.traverse_ {
      case id -> data =>
        ServicesSql.insertServiceTypeSql.queryUnique(id ~ data)
    }

  private def setupVisits(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    VisitsSql.insertItems(visits.values).execute(visits.values)

  private def setupDoctorShares(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    doctorShare.values.toList.traverse_ {
      case id -> data =>
        CheckupExpensesSql.insertDoctorShare.queryUnique(id ~ data)
    }

  private def setupCheckupExpenses(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    checkupExpense.values.traverse_ { data =>
      CheckupExpensesSql.insert.queryUnique(data)
    }

  private def setupOperationExpenses(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    operationExpenses.values.toList.traverse_ {
      case id -> data =>
        OperationExpensesSql
          .insert
          .queryUnique(
            OperationExpense(
              id = id,
              createdAt = LocalDateTime.now(),
              patientVisitId = data.patientVisitId,
              forLaboratory = data.forTools,
              forTools = data.forTools,
              forDrugs = data.forDrugs,
              partnerDoctorFullName = data.partnerDoctorFullName,
              partnerDoctorPrice = data.partnerDoctorPrice,
            )
          )
    }

  private def setupOperationExpenseItems(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    OperationExpensesSql
      .insertItems(operationExpenseItems.values)
      .execute(operationExpenseItems.values)
}
