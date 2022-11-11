package babymed.integrations.opersms

import java.net.URI

import ciris.Secret
import eu.timepit.refined.types.all.NonEmptyString

case class OperSmsConfig(
    apiURL: URI,
    login: NonEmptyString,
    password: Secret[NonEmptyString],
    enabled: Boolean = false,
  )
