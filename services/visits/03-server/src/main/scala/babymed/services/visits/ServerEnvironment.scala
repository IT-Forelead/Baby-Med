package babymed.services.visits

import babymed.services.visits.proto._

case class ServerEnvironment[F[_]](
    services: ServerEnvironment.Services[F]
  )

object ServerEnvironment {
  case class Services[F[_]](
      visits: Visits[F]
    )
}
