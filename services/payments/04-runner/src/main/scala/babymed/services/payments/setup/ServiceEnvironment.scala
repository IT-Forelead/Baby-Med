package babymed.services.payments.setup

import cats.MonadThrow
import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import babymed.services.payments.boundary.Payments
import babymed.services.payments.repositories.PaymentsRepository
import org.typelevel.log4cats.Logger
import skunk.Session

case class ServiceEnvironment[F[_]: MonadThrow](
    config: Config,
    payments: Payments[F],
  )

object ServiceEnvironment {

  def make[F[_]: Async: Console: Logger]: Resource[F, ServiceEnvironment[F]] =
    for {
      config <- Resource.eval(ConfigLoader.load[F])
      resource <- ServiceResources.make[F](config)
      paymentRepository = {
        implicit val session: Resource[F, Session[F]] = resource.postgres
        PaymentsRepository.make[F]
      }
    } yield ServiceEnvironment[F](config, new Payments[F](paymentRepository))
}
