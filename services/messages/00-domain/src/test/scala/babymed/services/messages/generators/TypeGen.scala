package babymed.services.messages.generators

import org.scalacheck.Gen

import babymed.domain.DeliveryStatus
import babymed.domain.MessageType
import babymed.services.messages.domain.types.MessageId
import babymed.services.messages.domain.types.MessageText
import babymed.syntax.refined.commonSyntaxAutoRefineV
import babymed.test.generators.Generators

trait TypeGen extends Generators {
  val messageIdGen: Gen[MessageId] = idGen(MessageId.apply)
  val messageTextGen: Gen[MessageText] = nonEmptyString.map(MessageText(_))
  val messageTypeGen: Gen[MessageType] = Gen.oneOf(MessageType.values)
  val deliveryStatusGen: Gen[DeliveryStatus] = Gen.oneOf(DeliveryStatus.values)
}
