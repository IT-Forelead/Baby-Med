package babymed.services.messages.setup

import cats.effect.Async
import cats.effect.Resource
import skunk.Session

import babymed.services.messages.repositories.MessagesRepository

case class Repositories[F[_]](
    messages: MessagesRepository[F]
  )
object Repositories {
  def make[F[_]: Async](
      implicit
      session: Resource[F, Session[F]]
    ): Repositories[F] =
    Repositories(
      messages = MessagesRepository.make[F]
    )
}
