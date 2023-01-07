package babymed.services.visits.repositories

import cats.data.OptionT
import cats.effect._
import cats.implicits._
import skunk._
import skunk.codec.all.int8
import skunk.implicits._
import squants.Money

import babymed.domain.ID
import babymed.effects.Calendar
import babymed.effects.GenUUID
import babymed.services.visits.domain._
import babymed.services.visits.domain.types.CheckupExpenseId
import babymed.services.visits.domain.types.DoctorShareId
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.domain.types.UZS
import babymed.services.visits.repositories.sql.CheckupExpensesSql
import babymed.support.skunk.syntax.all._

trait CheckupExpensesRepository[F[_]] {
  def create(createCheckupExpenses: List[CreateCheckupExpense]): F[List[CheckupExpense]]
  def createDoctorShare(createDoctorShare: CreateDoctorShare): F[DoctorShare]
  def get(filters: CheckupExpenseFilters): F[List[CheckupExpenseInfo]]
  def getTotal(filters: CheckupExpenseFilters): F[Long]
  def getDoctorShares: F[List[DoctorShareInfo]]
  def deleteDoctorShare(id: DoctorShareId): F[Unit]
}

object CheckupExpensesRepository {
  def make[F[_]: GenUUID: Calendar: Concurrent](
      implicit
      session: Resource[F, Session[F]]
    ): CheckupExpensesRepository[F] = new CheckupExpensesRepository[F] {
    import sql.CheckupExpensesSql._

    def createCheckupExpense(
        patientVisitId: PatientVisitId,
        doctorShareId: DoctorShareId,
        price: Money,
      ): F[CheckupExpense] =
      for {
        id <- ID.make[F, CheckupExpenseId]
        now <- Calendar[F].currentDateTime
        checkupExpense <- insert.queryUnique(
          CheckupExpense(
            id = id,
            createdAt = now,
            doctorShareId = doctorShareId,
            patientVisitId = patientVisitId,
            price = price,
          )
        )
      } yield checkupExpense

    override def create(
        createCheckupExpenses: List[CreateCheckupExpense]
      ): F[List[CheckupExpense]] =
      createCheckupExpenses.flatTraverse(chExp =>
        OptionT(selectDoctorShareByServiceId.queryOption(chExp.serviceId))
          .semiflatMap(doctorShare =>
            createCheckupExpense(
              patientVisitId = chExp.visitId,
              doctorShareId = doctorShare.doctorShare.id,
              price =
                UZS(doctorShare.service.price.value * doctorShare.doctorShare.percent.value / 100),
            )
          )
          .value
          .map(_.toList)
      )

    override def createDoctorShare(createDoctorShare: CreateDoctorShare): F[DoctorShare] =
      ID.make[F, DoctorShareId].flatMap { id =>
        insertDoctorShare.queryUnique(id ~ createDoctorShare)
      }

    override def get(filters: CheckupExpenseFilters): F[List[CheckupExpenseInfo]] = {
      val query = CheckupExpensesSql.select(filters).paginateOpt(filters.limit, filters.page)
      query.fragment.query(CheckupExpensesSql.decCheckupExpenseInfo).queryList(query.argument)
    }

    override def getTotal(filters: CheckupExpenseFilters): F[Long] = {
      val query = CheckupExpensesSql.total(filters)
      query.fragment.query(int8).queryUnique(query.argument)
    }

    override def getDoctorShares: F[List[DoctorShareInfo]] =
      selectDoctorSharesSql.all

    override def deleteDoctorShare(id: DoctorShareId): F[Unit] =
      deleteDoctorShareSql.execute(id)
  }
}
