package babymed.services.babymed.api

import java.time.LocalDateTime
import java.util.UUID

import scala.util.Try

import org.http4s.dsl.impl._

import babymed.services.payments.domain.types.PaymentId
import babymed.support.services.syntax.http4s.localDateTimeQueryParamDecoder
import babymed.util.MyPathVar

package object routes {
  object page extends QueryParamDecoderMatcherWithDefault[Int]("page", 1)
  object limit extends QueryParamDecoderMatcherWithDefault[Int]("limit", 30)
  object from extends OptionalQueryParamDecoderMatcher[LocalDateTime]("from")
  object to extends OptionalQueryParamDecoderMatcher[LocalDateTime]("to")
  object PaymentIdVar extends MyPathVar(str => Try(PaymentId(UUID.fromString(str))))
}
