package babymed.services.babymed.api.routes

import scala.concurrent.duration.DurationInt

import cats.effect.kernel.Sync
import ciris.Secret
import dev.profunktor.auth.jwt.JwtToken
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.Method.GET
import org.http4s.Method.POST
import org.http4s.Request
import org.http4s.Status
import org.http4s.Uri
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import org.scalacheck.Gen
import tsec.passwordhashers.jca.SCrypt
import weaver.Expectations

import babymed.domain.ResponseData
import babymed.domain.Role
import babymed.domain.Role.Doctor
import babymed.domain.Role.TechAdmin
import babymed.refinements.Phone
import babymed.services.auth.JwtConfig
import babymed.services.auth.domain.Credentials
import babymed.services.auth.domain.types._
import babymed.services.auth.impl.Security
import babymed.services.users.domain._
import babymed.services.users.domain.types.UserId
import babymed.services.users.generators.UserGenerators
import babymed.services.users.proto.Users
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.domain.PatientVisitInfo
import babymed.services.visits.domain.types
import babymed.services.visits.generators.PatientVisitGenerators
import babymed.services.visits.proto.Visits
import babymed.support.redis.RedisClientMock
import babymed.support.services.syntax.all._
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.test.HttpSuite

object VisitRoutersSpec extends HttpSuite with PatientVisitGenerators with UserGenerators {
  val jwtConfig: JwtConfig =
    JwtConfig(
      Secret(JwtAccessTokenKey(NonEmptyString.unsafeFrom("test"))),
      TokenExpiration(1.minutes),
    )

  lazy val user: User = userGen.get
  lazy val credentials: Credentials =
    Credentials(phoneGen.get, NonEmptyString.unsafeFrom(nonEmptyStringGen(8).get))
  lazy val patientVisit: PatientVisit = patientVisitGen.get
  lazy val patientVisitInfo: PatientVisitInfo = patientVisitInfoGen.get
  lazy val total: Long = Gen.long.get

  def users(role: Role): Users[F] = new Users[F] {
    override def find(phone: Phone): F[Option[UserAndHash]] =
      Sync[F].pure(
        Option(UserAndHash(user.copy(role = role), SCrypt.hashpwUnsafe(credentials.password)))
      )
    override def validationAndCreate(createUser: CreateUser): F[User] = Sync[F].delay(user)
    override def validationAndEdit(editUser: EditUser): F[Unit] = ???
    override def get(filters: UserFilters): F[ResponseData[User]] = ???
    override def delete(userId: UserId): F[Unit] = ???
    override def getTotal(filters: UserFilters): F[Long] = ???
    override def getSubRoles: F[List[SubRole]] = ???
  }

  val visits: Visits[F] = new Visits[F] {
    override def create(createPatientVisit: List[CreatePatientVisit]): F[Unit] =
      Sync[F].unit
    override def get(filters: PatientVisitFilters): F[ResponseData[PatientVisitInfo]] =
      Sync[F].delay(ResponseData(List(patientVisitInfo), total))
    override def getTotal(filters: PatientVisitFilters): F[Long] =
      Sync[F].delay(total)
    override def updatePaymentStatus(chequeId: types.ChequeId): F[Unit] =
      Sync[F].unit
  }

  def authedReq(
      role: Role = TechAdmin
    )(
      request: JwtToken => Request[F]
    )(
      expect: (Request[F], Security[F]) => F[Expectations]
    ): F[Expectations] = {
    val security = Security.make[F](jwtConfig, users(role), RedisClientMock[F])
    val loginReq = POST(credentials, uri"/auth/login")
    AuthRoutes[F](security)
      .routes
      .run(loginReq)
      .semiflatMap(_.as[JwtToken])
      .cataF(
        Sync[F].delay(failure("Should return jwt token")),
        token => expect(request(token), security),
      )
      .handleError {
        fail("Test failed")
      }
  }

  test("Create patient visit with incorrect role") {
    authedReq(Doctor) { token =>
      POST(createServiceGen().get, uri"/visit/create").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectNotFound(VisitRouters[F](security, visits).routes, request)
    }
  }

  test("Create patient visit with correct role") {
    authedReq() { token =>
      POST(List(createPatientVisitGen().get), uri"/visit/create").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectHttpStatus(VisitRouters[F](security, visits).routes, request)(Status.NoContent)
    }
  }

  test("Get All Patients visits") {
    authedReq() { token =>
      POST(PatientVisitFilters.Empty, uri"/visit/report").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectHttpBodyAndStatus(VisitRouters[F](security, visits).routes, request)(
          ResponseData(List(patientVisitInfo), total),
          Status.Ok,
        )
    }
  }

  test("Update patient status with incorrect role") {
    authedReq(Doctor) { token =>
      val patientVisitId = patientVisit.id
      GET(Uri.unsafeFromString(s"/visit/update-payment-status/$patientVisitId")).bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectNotFound(VisitRouters[F](security, visits).routes, request)
    }
  }

  test("Update patient status with correct role") {
    authedReq() { token =>
      val patientVisitId = patientVisit.id
      GET(Uri.unsafeFromString(s"/visit/update-payment-status/$patientVisitId")).bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectHttpStatus(VisitRouters[F](security, visits).routes, request)(Status.NoContent)
    }
  }
}
