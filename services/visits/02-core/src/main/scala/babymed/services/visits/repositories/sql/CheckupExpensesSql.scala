package babymed.services.visits.repositories.sql

import java.time.LocalDateTime

import skunk._
import skunk.codec.all._
import skunk.implicits.toIdOps
import skunk.implicits.toStringOps

import babymed.services.users.domain.User
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.CheckupExpenseId
import babymed.services.visits.domain.types.DoctorShareId
import babymed.support.skunk.codecs.percent
import babymed.support.skunk.codecs.phone
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps

object CheckupExpensesSql {
  private val Columns = checkupExpenseId ~ timestamp ~ doctorShareId ~ price ~ bool
  private val DoctorShareColumns = doctorShareId ~ serviceId ~ userId ~ percent ~ bool
  private val ServiceColumns = serviceId ~ serviceTypeId ~ serviceName ~ price ~ bool
  private val UserColumns =
    userId ~ timestamp ~ firstName ~ lastName ~ phone ~ role ~ subRoleId.opt ~ passwordHash ~ bool

  val encoder: Encoder[CheckupExpenseId ~ LocalDateTime ~ CreateCheckupExpense] =
    Columns.contramap {
      case id ~ createdAt ~ cce =>
        id ~ createdAt ~ cce.doctorShareId ~ cce.price ~ false
    }

  val decoder: Decoder[CheckupExpense] = Columns.map {
    case id ~ createdAt ~ doctorShareId ~ price ~ _ =>
      CheckupExpense(id, createdAt, doctorShareId, price)
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

  val decCheckupExpenseInfo: Decoder[CheckupExpenseInfo] =
    (decoder ~ decDoctorShare ~ decServiceWithTypeName ~ decUser).map {
      case checkupExpense ~ doctorShare ~ service ~ user =>
        CheckupExpenseInfo(checkupExpense, doctorShare, service, user)
    }

  val insert: Query[CheckupExpenseId ~ LocalDateTime ~ CreateCheckupExpense, CheckupExpense] =
    sql"""INSERT INTO checkup_expenses VALUES ($encoder) RETURNING *""".query(decoder)

  val insertDoctorShare: Query[DoctorShareId ~ CreateDoctorShare, DoctorShare] =
    sql"""INSERT INTO doctor_shares VALUES ($encDoctorShare) RETURNING *""".query(decDoctorShare)

  private def searchFilter(filters: CheckupExpenseFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"checkup_expenses.created_at >= $timestamp"),
      filters.endDate.map(sql"checkup_expenses.created_at <= $timestamp"),
      filters.serviceId.map(sql"doctor_shares.service_id = $serviceId"),
      filters.userId.map(sql"doctor_shares.user_id = $userId"),
    )

  def select(filters: CheckupExpenseFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT checkup_expenses.*, doctor_shares.*, services.*, service_types.name, users.*
        FROM checkup_expenses
        INNER JOIN doctor_shares ON checkup_expenses.doctor_share_id = doctor_shares.id
        INNER JOIN services ON doctor_shares.service_id = services.id
        INNER JOIN service_types ON services.service_type_id = service_types.id
        INNER JOIN users ON doctor_shares.user_id = users.id
        WHERE checkup_expenses.deleted = false"""

    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  def total(filters: CheckupExpenseFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT count(*) FROM operation_expenses
        INNER JOIN doctor_shares ON checkup_expenses.doctor_share_id = doctor_shares.id
        INNER JOIN services ON doctor_shares.service_id = services.id
        INNER JOIN users ON doctor_shares.user_id = users.id
        WHERE operation_expenses.deleted = false"""
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
}
