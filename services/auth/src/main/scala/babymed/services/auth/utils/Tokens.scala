package babymed.services.auth.utils

import cats.Monad
import cats.syntax.all._
import dev.profunktor.auth.jwt._
import eu.timepit.refined.auto._
import pdi.jwt._

import babymed.effects.GenUUID
import babymed.services.auth.domain.types.JwtAccessTokenKey
import babymed.services.auth.domain.types.TokenExpiration
import babymed.services.users.domain.User
import babymed.syntax.all.genericSyntaxGenericTypeOps
import babymed.syntax.all.optionSyntaxFunctorBooleanOps

trait Tokens[F[_]] {
  def create(user: User): F[JwtToken]
  def validateAndUpdate(claim: JwtClaim): F[Option[JwtToken]]
}

object Tokens {
  def make[F[_]: GenUUID: Monad](
      jwtExpire: JwtExpire[F],
      config: JwtAccessTokenKey,
      exp: TokenExpiration,
    ): Tokens[F] =
    new Tokens[F] {
      private def encodeToken: JwtClaim => F[JwtToken] =
        jwtEncode[F](_, JwtSecretKey(config.secret), JwtAlgorithm.HS256)

      override def create(user: User): F[JwtToken] =
        for {
          claim <- jwtExpire.expiresIn(JwtClaim(user.toJson), exp)
          token <- encodeToken(claim)
        } yield token

      override def validateAndUpdate(claim: JwtClaim): F[Option[JwtToken]] =
        jwtExpire
          .isExpired(claim)
          .asOptionT
          .semiflatMap { _ =>
            for {
              updated <- jwtExpire.expiresIn(claim, exp)
              token <- encodeToken(updated)
            } yield token
          }
          .value
    }
}
