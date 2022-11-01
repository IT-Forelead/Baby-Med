package babymed.services.auth.utils

import java.util.UUID

import scala.concurrent.duration.DurationInt

import pdi.jwt.JwtClaim
import weaver.SimpleIOSuite
import weaver.scalacheck.Checkers

import babymed.services.auth.domain.types.TokenExpiration

object JwtExpireSpec extends SimpleIOSuite with Checkers {
  test("Add token expiration") {
    JwtExpire[F]
      .expiresIn(JwtClaim(UUID.randomUUID().toString), TokenExpiration(1.minute))
      .map { claim =>
        assert(claim.expiration.nonEmpty)
      }
  }

  test("Token check expiration [ NOT EXPIRED ]") {
    for {
      claim <- JwtExpire[F]
        .expiresIn(JwtClaim(UUID.randomUUID().toString), TokenExpiration(1.minute))
      isExpired <- JwtExpire[F].isExpired(claim)
    } yield assert(!isExpired)
  }

  test("Token check expiration [ EXPIRED ]") {
    JwtExpire[F]
      .isExpired(
        JwtClaim(
          UUID.randomUUID().toString,
          expiration = Some(java.time.Clock.systemUTC().millis() / 1000 - 10),
        )
      )
      .map(isExpired => assert(isExpired))
  }
}
