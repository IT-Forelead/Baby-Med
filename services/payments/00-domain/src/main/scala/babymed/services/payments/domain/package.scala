package babymed.services.payments

import babymed.services.payments.domain.types.UZS
import cats.Eq
import cats.Monoid
import cats.Show
import io.circe.Decoder
import io.circe.Encoder
import squants.Money
import squants.market.Currency

package object domain {
  implicit val moneyDecoder: Decoder[Money] =
    Decoder[BigDecimal].map(UZS.apply)

  implicit val moneyEncoder: Encoder[Money] =
    Encoder[BigDecimal].contramap(_.amount)

  implicit val moneyMonoid: Monoid[Money] =
    new Monoid[Money] {
      def empty: Money = UZS(0)
      def combine(x: Money, y: Money): Money = x + y
    }

  implicit val currencyEq: Eq[Currency] =
    Eq.and(Eq.and(Eq.by(_.code), Eq.by(_.symbol)), Eq.by(_.name))

  implicit val moneyEq: Eq[Money] = Eq.and(Eq.by(_.amount), Eq.by(_.currency))

  implicit val moneyShow: Show[Money] = Show.fromToString
}
