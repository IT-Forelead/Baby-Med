package babymed.services.babymed.api

import babymed.services.users.proto._
import babymed.services.visits.proto
import babymed.services.visits.proto.OperationExpenses
import babymed.services.visits.proto.Visits

case class Services[F[_]](
    users: Users[F],
    patients: Patients[F],
    services: proto.Services[F],
    visits: Visits[F],
    operationExpenses: OperationExpenses[F],
  )
