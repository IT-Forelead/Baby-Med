package babymed.support.mailer.data

import babymed.refinements.EmailAddress
import babymed.support.mailer.data.types.Password

case class Credentials(user: EmailAddress, password: Password)
