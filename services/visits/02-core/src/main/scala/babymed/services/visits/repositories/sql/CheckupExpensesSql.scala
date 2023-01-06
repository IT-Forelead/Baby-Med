package babymed.services.visits.repositories.sql

import skunk._
import skunk.codec.all._
import skunk.implicits.toIdOps
import skunk.implicits.toStringOps

import babymed.services.users.domain.User
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.DoctorShareId
import babymed.services.visits.domain.types.ServiceId
import babymed.support.skunk.codecs.percent
import babymed.support.skunk.codecs.phone
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps

object CheckupExpensesSql {
  private val Columns = checkupExpenseId ~ timestamp ~ doctorShareId ~ patientVisitId ~ price ~ bool
  private val DoctorShareColumns = doctorShareId ~ serviceId ~ userId ~ percent ~ bool
  private val ServiceColumns = serviceId ~ serviceTypeId ~ serviceName ~ price ~ bool
  private val UserColumns =
    userId ~ timestamp ~ firstName ~ lastName ~ phone ~ role ~ subRoleId.opt ~ passwordHash ~ bool

  val encoder: Encoder[CheckupExpense] =
    Columns.contramap(ce =>
      ce.id ~ ce.createdAt ~ ce.doctorShareId ~ ce.patientVisitId ~ ce.price ~ false
    )

  val decoder: Decoder[CheckupExpense] = Columns.map {
    case id ~ createdAt ~ doctorShareId ~ patientVisitId ~ price ~ _ =>
      CheckupExpense(id, createdAt, doctorShareId, patientVisitId, price)
  }

  val encDoctorShare: Encoder[DoctorShareId ~ CreateDoctorShare] =
    DoctorShareColumns.contramap {
      case id ~ cds =>
        id ~ cds.serviceId ~ cds.userId ~ cds.percent ~ false
    }

  val decServiceWithTypeName: Decoder[ServiceWithTypeName] =
    (ServiceColumns ~ serviceTypeName).map {
      case id ~ serviceTypeId ~ name ~ price ~ _ ~ serviceTypeName =>
        ServiceWithTypeName(id, serviceTypeId, name, price, serviceTypeName)
    }

  val decService: Decoder[Service] = ServiceColumns.map {
    case id ~ serviceTypeId ~ name ~ price ~ _ =>
      Service(id, serviceTypeId, name, price)
  }

  val decUser: Decoder[User] = UserColumns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ phone ~ role ~ subRoleId ~ _ ~ _ =>
      User(id, createdAt, firstName, lastName, phone, role, subRoleId)
  }

  val decDoctorShare: Decoder[DoctorShare] = DoctorShareColumns.map {
    case id ~ serviceId ~ userId ~ percent ~ _ =>
      DoctorShare(id, serviceId, userId, percent)
  }

  val decDoctorShareInfo: Decoder[DoctorShareInfo] =
    (decDoctorShare ~ decServiceWithTypeName ~ decUser).map {
      case doctorShare ~ service ~ user =>
        DoctorShareInfo(doctorShare, service, user)
    }

  val decDoctorShareWithService: Decoder[DoctorShareWithService] =
    (decDoctorShare ~ decService).map {
      case doctorShare ~ service =>
        DoctorShareWithService(doctorShare, service)
    }

  val decCheckupExpenseInfo: Decoder[CheckupExpenseInfo] =
    (decoder ~ decDoctorShare ~ decServiceWithTypeName ~ decUser ~ VisitsSql.decoder ~ VisitsSql.decPatient)
      .map {
        case checkupExpense ~ doctorShare ~ service ~ user ~ visit ~ patient =>
          CheckupExpenseInfo(checkupExpense, doctorShare, service, user, visit, patient)
      }

  val insert: Query[CheckupExpense, CheckupExpense] =
    sql"""INSERT INTO checkup_expenses VALUES ($encoder) RETURNING *""".query(decoder)

  val insertDoctorShare: Query[DoctorShareId ~ CreateDoctorShare, DoctorShare] =
    sql"""INSERT INTO doctor_shares VALUES ($encDoctorShare) RETURNING *""".query(decDoctorShare)

  private def searchFilter(filters: CheckupExpenseFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"checkup_expenses.created_at >= $timestamp"),
      filters.endDate.map(sql"checkup_expenses.created_at <= $timestamp"),
      filters.patientVisitId.map(sql"checkup_expenses.visit_id = $patientVisitId"),
      filters.serviceId.map(sql"doctor_shares.service_id = $serviceId"),
      filters.userId.map(sql"doctor_shares.user_id = $userId"),
    )

  def select(filters: CheckupExpenseFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT checkup_expenses.*, doctor_shares.*, services.*, service_types.name, users.*, visits.*, patients.*
        FROM checkup_expenses
        INNER JOIN doctor_shares ON checkup_expenses.doctor_share_id = doctor_shares.id
        INNER JOIN visits ON checkup_expenses.visit_id = visits.id
        INNER JOIN patients ON visits.patient_id = patients.id
        INNER JOIN services ON doctor_shares.service_id = services.id
        INNER JOIN service_types ON services.service_type_id = service_types.id
        INNER JOIN users ON doctor_shares.user_id = users.id
        WHERE checkup_expenses.deleted = false"""

    baseQuery(Void).andOpt(searchFilter(filters): _*) |+| sql" ORDER BY visits.created_at DESC"
      .apply(Void)
  }

  def total(filters: CheckupExpenseFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT count(*) FROM checkup_expenses
        INNER JOIN visits ON checkup_expenses.visit_id = visits.id
        INNER JOIN doctor_shares ON checkup_expenses.doctor_share_id = doctor_shares.id
        WHERE checkup_expenses.deleted = false"""
    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  val selectDoctorSharesSql: Query[Void, DoctorShareInfo] =
    sql"""SELECT doctor_shares.*, services.*, service_types.name, users.*
        FROM doctor_shares
        INNER JOIN services ON doctor_shares.service_id = services.id
        INNER JOIN service_types ON services.service_type_id = service_types.id
        INNER JOIN users ON doctor_shares.user_id = users.id
        WHERE doctor_shares.deleted = false"""
      .query(decDoctorShareInfo)

  val selectDoctorShareByServiceId: Query[ServiceId, DoctorShareWithService] =
    sql"""SELECT doctor_shares.*, services.* FROM doctor_shares
        INNER JOIN services ON doctor_shares.service_id = services.id
        WHERE doctor_shares.service_id = $serviceId AND doctor_shares.deleted = false"""
      .query(decDoctorShareWithService)

  val deleteDoctorShareSql: Command[DoctorShareId] =
    sql"""UPDATE doctor_shares SET deleted = true WHERE id = $doctorShareId""".command
}
