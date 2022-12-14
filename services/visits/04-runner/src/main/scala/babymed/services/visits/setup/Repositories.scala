package babymed.services.visits.setup

import cats.effect.Async
import cats.effect.Resource
import skunk.Session

import babymed.services.visits.repositories._

case class Repositories[F[_]](
    operationExpenses: OperationExpensesRepository[F],
    services: ServicesRepository[F],
    visits: VisitsRepository[F],
    checkupExpenses: CheckupExpensesRepository[F],
  )
object Repositories {
  def make[F[_]: Async](
      implicit
      session: Resource[F, Session[F]]
    ): Repositories[F] =
    Repositories(
      operationExpenses = OperationExpensesRepository.make[F],
      services = ServicesRepository.make[F],
      visits = VisitsRepository.make[F],
      checkupExpenses = CheckupExpensesRepository.make[F],
    )
}
