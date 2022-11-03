package babymed.services.babymed.api

import babymed.support.services.syntax.http4s.localDateTimeQueryParamDecoder
import org.http4s.dsl.impl._

import java.time.LocalDateTime

package object routes {
  object page extends QueryParamDecoderMatcherWithDefault[Int]("page", 1)
  object limit extends QueryParamDecoderMatcherWithDefault[Int]("limit", 30)
  object from extends OptionalQueryParamDecoderMatcher[LocalDateTime]("from")
  object to extends OptionalQueryParamDecoderMatcher[LocalDateTime]("to")

}
