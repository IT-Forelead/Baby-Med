package babymed.services.users

import babymed.services.messages.proto.Messages

case class Services[F[_]](
    messages: Messages[F]
  )
