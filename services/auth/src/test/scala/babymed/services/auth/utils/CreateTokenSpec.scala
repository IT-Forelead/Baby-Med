package babymed.services.auth.utils

import scala.concurrent.duration.DurationInt

import pdi.jwt.JwtClaim
import weaver.SimpleIOSuite
import weaver.scalacheck.Checkers

import babymed.services.auth.AuthServiceSpec.userGen
import babymed.services.auth.domain.types.JwtAccessTokenKey
import babymed.services.auth.domain.types.TokenExpiration
import babymed.services.users.domain.User
import babymed.syntax.all.genericSyntaxGenericTypeOps
import babymed.syntax.refined.commonSyntaxAutoRefineV

object CreateTokenSpec extends SimpleIOSuite with Checkers {
  lazy val user: User = userGen.sample.get
  test("Create jwt token") {
    Tokens
      .make[F](JwtExpire[F], JwtAccessTokenKey("test"), TokenExpiration(1.minute))
      .create(user)
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
        .expiresIn(JwtClaim(user.toJson), expiration)
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
          user.toJson,
          expiration = Some(java.time.Clock.systemUTC().millis() / 1000 - 1),
        )
      )
      .map(maybeToken => assert(maybeToken.nonEmpty))
  }
}
