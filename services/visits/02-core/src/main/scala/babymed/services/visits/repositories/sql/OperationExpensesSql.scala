package babymed.services.visits.repositories.sql

import skunk._
import skunk.codec.all._
import skunk.implicits.toIdOps
import skunk.implicits.toStringOps

import babymed.services.users.domain.SubRole
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.OperationExpenseId
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.repositories.sql.CheckupExpensesSql.decServiceWithTypeName
import babymed.services.visits.repositories.sql.CheckupExpensesSql.decUser
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps

object OperationExpensesSql {
  private val Columns =
    operationExpenseId ~ timestamp ~ operationId ~ price ~ price ~ price ~ partnerDoctorFullName.opt ~ price.opt ~ bool
  private val OperationColumns = operationId ~ timestamp ~ patientId ~ serviceId ~ bool
  private val ItemsColumns = operationExpenseId ~ userId ~ subRoleId ~ price ~ bool
  private val OperationServiceColumns = operationServiceId ~ serviceId ~ bool

  val encoder: Encoder[OperationExpense] =
    Columns.contramap(oe =>
      oe.id ~ oe.createdAt ~ oe.operationId ~ oe.forLaboratory ~ oe.forTools ~ oe.forDrugs ~ oe.partnerDoctorFullName ~ oe.partnerDoctorPrice ~ false
    )

  val decoder: Decoder[OperationExpense] = Columns.map {
    case id ~ createdAt ~ operationId ~ forLaboratory ~ forTools ~ forDrugs ~ partnerDoctorFullName ~ partnerDoctorPrice ~ _ =>
      OperationExpense(
        id,
        createdAt,
        operationId,
        forLaboratory,
        forTools,
        forDrugs,
        partnerDoctorFullName,
        partnerDoctorPrice,
      )
  }

  val decOperation: Decoder[Operation] = OperationColumns.map {
    case id ~ createdAt ~ patientId ~ serviceId ~ _ =>
      Operation(id, createdAt, patientId, serviceId)
  }

  val encItem: Encoder[OperationExpenseItem] =
    ItemsColumns.contramap(oei =>
      oei.operationExpenseId ~ oei.userId ~ oei.subRoleId ~ oei.price ~ false
    )

  val encOperation: Encoder[Operation] =
    OperationColumns.contramap(o => o.id ~ o.createdAt ~ o.patientId ~ o.serviceId ~ false)

  val decItem: Decoder[OperationExpenseItem] = ItemsColumns.map {
    case operationExpenseId ~ userId ~ subRoleId ~ price ~ _ =>
      OperationExpenseItem(operationExpenseId, userId, subRoleId, price)
  }

  val encOperationService: Encoder[OperationService] =
    OperationServiceColumns.contramap(os => os.id ~ os.serviceId ~ false)

  val decOperationService: Decoder[OperationService] = OperationServiceColumns.map {
    case id ~ serviceId ~ _ =>
      OperationService(id, serviceId)
  }

  val decOperationServiceInfo: Decoder[OperationServiceInfo] =
    (decOperationService ~ decServiceWithTypeName).map {
      case operationService ~ service =>
        OperationServiceInfo(operationService, service)
    }

  val decSubRole: Decoder[SubRole] = (subRoleId ~ subRoleName ~ bool).map {
    case id ~ name ~ _ =>
      SubRole(id, name)
  }

  val decItemWithUser: Decoder[OperationExpenseItemWithUser] =
    (decItem ~ decUser ~ decSubRole).map {
      case item ~ user ~ subRole =>
        OperationExpenseItemWithUser(item, user, subRole)
    }

  val decOperationExpenseInfo: Decoder[OperationExpenseInfo] =
    (decoder ~ decOperation ~ VisitsSql.decPatient ~ ServicesSql.decServiceWithTypeName).map {
      case operationExpense ~ operation ~ patient ~ service =>
        OperationExpenseInfo(operationExpense, operation, patient, service)
    }

  val decOperationInfo: Decoder[OperationInfo] =
    (decOperation ~ VisitsSql.decServiceWithTypeName ~ VisitsSql.decPatient ~ VisitsSql.decRegion ~ VisitsSql.decCity)
      .map {
        case operation ~ service ~ patient ~ region ~ city =>
          OperationInfo(operation, service, patient, region, city)
      }

  val insert: Query[OperationExpense, OperationExpense] =
    sql"""INSERT INTO operation_expenses VALUES ($encoder) RETURNING *""".query(decoder)

  def insertItems(item: List[OperationExpenseItem]): Command[item.type] = {
    val enc = encItem.values.list(item)
    sql"""INSERT INTO operation_expense_items VALUES $enc""".command
  }

  def insertOperations(operation: List[Operation]): Command[operation.type] = {
    val enc = encOperation.values.list(operation)
    sql"""INSERT INTO operations VALUES $enc""".command
  }

  val insertOperationService: Query[OperationService, OperationService] =
    sql"""INSERT INTO operation_services VALUES ($encOperationService) RETURNING *"""
      .query(decOperationService)

  private def searchFilter(filters: OperationExpenseFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"operation_expenses.created_at >= $timestamp"),
      filters.endDate.map(sql"operation_expenses.created_at <= $timestamp"),
    )

  def select(filters: OperationExpenseFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT operation_expenses.*, operations.*, patients.*, services.*, service_types.name
        FROM operation_expenses
        INNER JOIN operations ON operation_expenses.operation_id = operations.id
        INNER JOIN patients ON operations.patient_id = patients.id
        INNER JOIN services ON operations.service_id = services.id
        INNER JOIN service_types ON services.service_type_id = service_types.id
        WHERE operation_expenses.deleted = false"""

    baseQuery(Void).andOpt(
      searchFilter(filters): _*
    ) |+| sql" ORDER BY operation_expenses.created_at DESC".apply(Void)
  }

  def total(filters: OperationExpenseFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT count(*) FROM operation_expenses WHERE deleted = false"""
    baseQuery(Void).andOpt(searchFilter(filters): _*)
  }

  private def operationFilters(filters: OperationFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"operations.created_at >= $timestamp"),
      filters.endDate.map(sql"operations.created_at <= $timestamp"),
      filters.patientId.map(sql"operations.patient_id = $patientId"),
      filters.serviceId.map(sql"operations.service_id = $serviceId"),
    )

  def selectOperations(filters: OperationFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT operations.*, services.*, service_types.name, patients.*, regions.*, cities.*
        FROM operations
        INNER JOIN patients ON operations.patient_id = patients.id
        INNER JOIN services ON operations.service_id = services.id
        INNER JOIN service_types ON services.service_type_id = service_types.id
        INNER JOIN regions ON patients.region_id = regions.id
        INNER JOIN cities  on patients.city_id = cities.id
        WHERE operations.deleted = false"""

    baseQuery(Void).andOpt(
      operationFilters(filters): _*
    ) |+| sql" ORDER BY operations.created_at DESC".apply(Void)
  }

  def operationTotal(filters: OperationFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT count(*) FROM operations WHERE deleted = false"""
    baseQuery(Void).andOpt(operationFilters(filters): _*)
  }

  val selectItemsSql: Query[OperationExpenseId, OperationExpenseItemWithUser] =
    sql"""SELECT operation_expense_items.*, users.*, sub_roles.*
        FROM operation_expense_items
        INNER JOIN users ON operation_expense_items.user_id = users.id
        INNER JOIN sub_roles ON operation_expense_items.sub_role_id = sub_roles.id
        WHERE operation_expense_items.operation_expense_id = $operationExpenseId
        AND operation_expense_items.deleted = false"""
      .query(decItemWithUser)

  val selectOperationServices: Query[Void, OperationServiceInfo] =
    sql"""SELECT operation_services.*, services.*, service_types.name
        FROM operation_services
        INNER JOIN services ON operation_services.service_id = services.id
        INNER JOIN service_types ON services.service_type_id = service_types.id
        WHERE operation_services.deleted = false"""
      .query(decOperationServiceInfo)

  val selectOperationServiceIds: Query[Void, ServiceId] =
    sql"""SELECT service_id FROM operation_services WHERE deleted = false"""
      .query(serviceId)
}
