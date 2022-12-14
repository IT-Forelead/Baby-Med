package babymed.services.messages.repositories

import cats.effect.IO

import babymed.integrations.opersms.domain.DeliveryStatus._
import babymed.services.messages.generators.MessageGenerators
import babymed.support.database.DBSuite

object MessagesRepositorySpec extends DBSuite with MessageGenerators {
  override def schemaName: String = "public"

  test("Create Message") { implicit postgres =>
    val create = createMessageGen.get
    MessagesRepository
      .make[F]
      .create(create)
      .map { s =>
        assert(s.text == create.text)
      }
      .handleError {
        fail("Test failed.")
      }
  }

  test("Change Delivery Status") { implicit postgres =>
    val repo = MessagesRepository.make[IO]
    for {
      crm <- repo.create(createMessageGen.get.copy(deliveryStatus = SENT))
      chm <- repo.changeStatus(crm.id, DELIVERED)
    } yield assert(chm.deliveryStatus == DELIVERED)
  }
}
