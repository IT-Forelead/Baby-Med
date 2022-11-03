package babymed.services.babymed.api

import java.time.LocalDateTime

import org.http4s.dsl.impl._

import babymed.support.services.syntax.http4s.localDateTimeQueryParamDecoder

package object routes {
  object page extends QueryParamDecoderMatcherWithDefault[Int]("page", 1)
  object limit extends QueryParamDecoderMatcherWithDefault[Int]("limit", 30)
  object from extends OptionalQueryParamDecoderMatcher[LocalDateTime]("from")
  object to extends OptionalQueryParamDecoderMatcher[LocalDateTime]("to")
}
