package babymed.services.messages.boundary

import cats.effect.kernel.Sync

import babymed.integrations.opersms.domain.DeliveryStatus
import babymed.services.messages.domain.CreateMessage
import babymed.services.messages.domain.Message
import babymed.services.messages.domain.types.MessageId
import babymed.services.messages.generators.MessageGenerators
import babymed.services.messages.repositories.MessagesRepository
import babymed.test.TestSuite

object MessagesSpec extends TestSuite with MessageGenerators {
  val messageRepo: MessagesRepository[F] = new MessagesRepository[F] {
    override def create(createMessage: CreateMessage): F[Message] =
      Sync[F].delay(messageGen.get)
    override def changeStatus(id: MessageId, deliveryStatus: DeliveryStatus): F[Message] =
      Sync[F].delay(messageGen.get)
  }

  val messages: Messages[F] = new Messages[F](messageRepo)

  loggedTest("Create Message") { logger =>
    messages
      .create(createMessageGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Change Delivery Status") { logger =>
    messages
      .changeStatus(messageIdGen.get, deliveryStatusGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }
}
