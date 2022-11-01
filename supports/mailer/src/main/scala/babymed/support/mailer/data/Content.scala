package babymed.support.mailer.data

case class Content(
    text: Option[Text] = None,
    html: Option[Html] = None,
  )
