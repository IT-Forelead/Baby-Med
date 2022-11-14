package babymed.services.babymed.api

import babymed.services.payments.proto._
import babymed.services.users.proto._

case class Services[F[_]](
    users: Users[F],
    patients: Patients[F],
    payments: Payments[F],
  )
