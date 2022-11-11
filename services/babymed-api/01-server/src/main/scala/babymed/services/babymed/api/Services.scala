package babymed.services.babymed.api

import babymed.services.users.proto._
import babymed.services.visits.proto._

case class Services[F[_]](
    users: Users[F],
    customers: Customers[F],
    visits: Visits[F],
  )
