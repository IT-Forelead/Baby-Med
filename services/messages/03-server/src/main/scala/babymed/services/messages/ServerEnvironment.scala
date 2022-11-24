package babymed.services.messages

import babymed.services.messages.proto.Messages

case class ServerEnvironment[F[_]](
    services: ServerEnvironment.Services[F]
  )

object ServerEnvironment {
  case class Services[F[_]](
      messages: Messages[F]
    )
}
