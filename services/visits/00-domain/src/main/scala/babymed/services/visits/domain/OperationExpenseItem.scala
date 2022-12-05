package babymed.services.visits.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import squants.Money

import babymed.services.users.domain.types.SubRoleId
import babymed.services.users.domain.types.UserId
import babymed.services.visits.domain.types.OperationExpenseId

@derive(decoder, encoder)
case class OperationExpenseItem(
    operationExpenseId: OperationExpenseId,
    userId: UserId,
    subRoleId: SubRoleId,
    price: Money,
  )
