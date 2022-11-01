package babymed.util

import java.net.URI

import ciris.ConfigDecoder

import babymed.domain.AppMode

object ConfigDecoders {
  implicit val javaNetUriConfigDecoder: ConfigDecoder[String, URI] =
    ConfigDecoder[String].map { uri =>
      URI.create(uri)
    }
  implicit val appModeConfigDecoder: ConfigDecoder[String, AppMode] =
    ConfigDecoder[String].mapOption("AppMode") { mode =>
      AppMode.find(mode)
    }
}
