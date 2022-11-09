package babymed.services.users

import babymed.services.users.proto._

case class ServerEnvironment[F[_]](
    services: ServerEnvironment.Services[F]
  )

object ServerEnvironment {
  case class Services[F[_]](
      users: Users[F],
      customers: Customers[F],
    )
}
