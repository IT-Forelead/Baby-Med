package babymed.support.mailer.data

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

import eu.timepit.refined.types.string.NonEmptyString

import babymed.support.mailer.data.types.Subtype.HTML
import babymed.support.mailer.data.types._

case class Html(
    value: NonEmptyString,
    charset: Charset = StandardCharsets.UTF_8,
    subtype: Subtype = HTML,
    headers: List[Header] = Nil,
  )
