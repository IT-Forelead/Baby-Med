package babymed.services.users.repositories.sql

import babymed.refinements.Phone
import babymed.services.users.domain.{User, UserAndHash}
import babymed.services.users.domain.types.UserId
import babymed.support.skunk.codecs.phone
import skunk._
import skunk.implicits._

object UsersSql {
  val userId: Codec[UserId] = identity[UserId]

  private val ColumnsWitPassword = userId ~ firstName ~ lastName ~ phone ~ role ~ passwordHash

  val decoderUserAndHash: Decoder[UserAndHash] = ColumnsWitPassword.map {
    case id ~ firstName ~ lastName ~ phone ~ role ~ password =>
      UserAndHash(
        user = User(id, firstName, lastName, phone, role),
        password = password
      )
  }

  val selectByPhone: Query[Phone, UserAndHash] =
    sql"""SELECT * FROM users WHERE phone = $phone""".query(decoderUserAndHash)

}
