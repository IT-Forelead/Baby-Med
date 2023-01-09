package babymed.services.babymed.api.routes

import cats.effect.Async
import cats.implicits._
import org.http4s.AuthedRoutes
import org.http4s.HttpRoutes
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger
import babymed.domain.Role.Cashier
import babymed.domain.Role.SuperManager
import babymed.domain.Role.TechAdmin
import babymed.services.auth.impl.Security
import babymed.services.users.domain.User
import babymed.services.visits.domain.{CreateOperationExpense, OperationExpenseFilters, OperationFilters}
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.proto.OperationExpenses
import babymed.support.services.syntax.all.deriveEntityEncoder
import babymed.support.services.syntax.all.http4SyntaxReqOps

final case class OperationExpenseRouters[F[_]: Async: JsonDecoder](
    security: Security[F],
    operationExpenses: OperationExpenses[F],
  )(implicit
    logger: Logger[F]
  ) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/operation-expense"

  private[this] val privateRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {

    case ar @ POST -> Root / "create" as user
         if List(SuperManager, Cashier, TechAdmin).contains(user.role) =>
      ar.req.decodeR[CreateOperationExpense] { operationExpense =>
        operationExpenses.create(operationExpense) *> NoContent()
      }

    case ar @ POST -> Root / "report" as user
         if List(SuperManager, Cashier, TechAdmin).contains(user.role) =>
      ar.req.decodeR[OperationExpenseFilters] { operationExpenseFilters =>
        operationExpenses
          .get(operationExpenseFilters)
          .flatMap(Ok(_))
      }

    case ar @ POST -> Root / "operations" as user
         if List(SuperManager, Cashier, TechAdmin).contains(user.role) =>
      ar.req.decodeR[OperationFilters] { operationFilters =>
        operationExpenses
          .getOperations(operationFilters)
          .flatMap(Ok(_))
      }

    case ar @ POST -> Root / "report" / "summary" as user
         if List(SuperManager, Cashier, TechAdmin).contains(user.role) =>
      ar.req.decodeR[OperationExpenseFilters] { operationExpenseFilters =>
        operationExpenses.getTotal(operationExpenseFilters).flatMap(Ok(_))
      }

    case GET -> Root / "items" / OperationExpenseIdVar(id) as user
         if List(SuperManager, Cashier, TechAdmin).contains(user.role) =>
      operationExpenses.getItemsById(id).flatMap(Ok(_))

    case ar @ POST -> Root / "create" / "operation-service" as user
         if List(SuperManager, TechAdmin).contains(user.role) =>
      ar.req.decodeR[ServiceId] { serviceId =>
        operationExpenses.createOperationServices(serviceId) *> NoContent()
      }

    case GET -> Root / "operation-services" as _ =>
      operationExpenses.getOperationServices.flatMap(Ok(_))

  }

  lazy val routes: HttpRoutes[F] = Router(
    prefixPath -> security.auth.usersMiddleware(security.userJwtAuth)(privateRoutes)
  )
}
