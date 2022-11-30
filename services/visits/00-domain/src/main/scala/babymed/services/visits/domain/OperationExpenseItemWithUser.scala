package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.services.users.domain.SubRole
import babymed.services.users.domain.User

@derive(decoder, encoder)
case class OperationExpenseItemWithUser(
    item: OperationExpenseItem,
    user: User,
    subRole: SubRole,
  )
