package babymed.support.mailer.data

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

import babymed.support.mailer.data.types.Subtype.HTML
import babymed.support.mailer.data.types._
import eu.timepit.refined.types.string.NonEmptyString

case class Html(
    value: NonEmptyString,
    charset: Charset = StandardCharsets.UTF_8,
    subtype: Subtype = HTML,
    headers: List[Header] = Nil,
  )
