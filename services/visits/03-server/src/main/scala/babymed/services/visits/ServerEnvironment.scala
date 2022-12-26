package babymed.services.visits

import babymed.services.visits.proto._

case class ServerEnvironment[F[_]](
    services: ServerEnvironment.Services[F]
  )

object ServerEnvironment {
  case class Services[F[_]](
      operationExpenses: OperationExpenses[F],
      services: proto.Services[F],
      visits: Visits[F],
      checkupExpenses: CheckupExpenses[F],
    )
}
