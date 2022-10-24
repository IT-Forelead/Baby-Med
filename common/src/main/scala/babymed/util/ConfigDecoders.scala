package babymed.util

import java.net.URI

import babymed.domain.AppMode
import ciris.ConfigDecoder

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
