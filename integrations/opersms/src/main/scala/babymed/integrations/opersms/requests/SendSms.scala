package babymed.integrations.opersms.requests

import cats.data.NonEmptyList
import eu.timepit.refined.types.string.NonEmptyString
import sttp.model.Method

import babymed.integrations.opersms.domain.SMS
import babymed.integrations.opersms.domain.SmsResponse
import babymed.support.sttp.SttpRequest
import babymed.syntax.generic.genericSyntaxGenericTypeOps
import babymed.syntax.refined.commonSyntaxAutoUnwrapV

case class SendSms(
    login: NonEmptyString,
    password: NonEmptyString,
    sms: NonEmptyList[SMS],
  )

object SendSms {
  implicit val sttpRequest: SttpRequest[SendSms, List[SmsResponse]] =
    new SttpRequest[SendSms, List[SmsResponse]] {
      val method: Method = Method.POST
      val path: Path = _ => "/sms"
      def body: Body = formBody { req =>
        Map(
          "login" -> req.login,
          "password" -> req.password.value,
          "data" -> req.sms.toJson,
        )
      }
    }
}
