package babymed.services.auth.utils

import java.util.UUID

import scala.concurrent.duration.DurationInt

import pdi.jwt.JwtClaim
import weaver.SimpleIOSuite
import weaver.scalacheck.Checkers

import babymed.services.auth.domain.types.JwtAccessTokenKey
import babymed.services.auth.domain.types.TokenExpiration
import babymed.syntax.refined.commonSyntaxAutoRefineV

object CreateTokenSpec extends SimpleIOSuite with Checkers {
  test("Create jwt token") {
    Tokens
      .make[F](JwtExpire[F], JwtAccessTokenKey("test"), TokenExpiration(1.minute))
      .create
      .map { token =>
        assert(token.value.nonEmpty)
      }
      .handleError {
        fail("Should be create token")
      }
  }

  test("Should not create new jwt token") {
    val expiration = TokenExpiration(1.minute)
    for {
      claim <- JwtExpire[F]
        .expiresIn(JwtClaim(UUID.randomUUID().toString), expiration)
      maybeToken <- Tokens
        .make[F](JwtExpire[F], JwtAccessTokenKey("test"), expiration)
        .validateAndUpdate(claim)
    } yield assert(maybeToken.isEmpty)
  }

  test("Should create new jwt token") {
    Tokens
      .make[F](JwtExpire[F], JwtAccessTokenKey("test"), TokenExpiration(1.minute))
      .validateAndUpdate(
        JwtClaim(
          UUID.randomUUID().toString,
          expiration = Some(java.time.Clock.systemUTC().millis() / 1000 - 1),
        )
      )
      .map(maybeToken => assert(maybeToken.nonEmpty))
  }
}
