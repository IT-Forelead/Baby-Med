package babymed.services.babymed.api

import babymed.services.payments.proto._
import babymed.services.users.proto._

case class Services[F[_]](
    users: Users[F],
    customers: Customers[F],
    payments: Payments[F],
  )
