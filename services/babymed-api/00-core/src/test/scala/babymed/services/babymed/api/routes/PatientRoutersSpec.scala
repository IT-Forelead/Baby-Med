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
import babymed.services.users.domain.types.Fullname
import babymed.services.users.domain.types.PatientId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.UserId
import babymed.services.users.generators.PatientGenerators
import babymed.services.users.generators.UserGenerators
import babymed.services.users.proto.Patients
import babymed.services.users.proto.Users
import babymed.support.redis.RedisClientMock
import babymed.support.services.syntax.all._
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.test.HttpSuite

object PatientRoutersSpec extends HttpSuite with PatientGenerators with UserGenerators {
  val jwtConfig: JwtConfig =
    JwtConfig(
      Secret(JwtAccessTokenKey(NonEmptyString.unsafeFrom("test"))),
      TokenExpiration(1.minutes),
    )

  lazy val user: User = userGen.get
  lazy val credentials: Credentials =
    Credentials(phoneGen.get, NonEmptyString.unsafeFrom(nonEmptyStringGen(8).get))
  lazy val patient: Patient = patientGen.get
  lazy val patientWithAddress: PatientWithAddress = patientWithAddressGen.get
  lazy val region: Region = regionGen.get
  lazy val city: City = cityGen.get
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

  val patients: Patients[F] = new Patients[F] {
    override def create(createPatient: CreatePatient): F[Patient] =
      Sync[F].delay(patient)
    override def getPatients(filters: PatientFilters): F[ResponseData[PatientWithAddress]] =
      Sync[F].delay(ResponseData(List(patientWithAddress), total))
    override def getTotalPatients(filters: PatientFilters): F[Long] =
      Sync[F].delay(total)
    override def getPatientById(patient: PatientId): F[Option[PatientWithAddress]] =
      Sync[F].delay(Option(patientWithAddress))
    override def getRegions: F[List[Region]] = Sync[F].delay(List(region))
    override def getCitiesByRegionId(regionId: RegionId): F[List[City]] =
      Sync[F].delay(List(city))
    override def getPatientsByName(name: Fullname): F[List[PatientWithName]] =
      ???
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

  test("Create patient with incorrect role") {
    authedReq(Doctor) { token =>
      POST(createPatientGen().get, uri"/patient").bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectNotFound(PatientRouters[F](security, patients).routes, request)
    }
  }

  test("Create patient with correct role") {
    authedReq() { token =>
      POST(createPatientGen().get, uri"/patient").bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpStatus(PatientRouters[F](security, patients).routes, request)(Status.NoContent)
    }
  }

  test("Get patients") {
    authedReq() { token =>
      POST(PatientFilters.Empty, uri"/patient/report").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectHttpBodyAndStatus(PatientRouters[F](security, patients).routes, request)(
          ResponseData(List(patientWithAddress), total),
          Status.Ok,
        )
    }
  }

  test("Get patients total") {
    authedReq() { token =>
      POST(PatientFilters.Empty, uri"/patient/report/summary").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectHttpBodyAndStatus(PatientRouters[F](security, patients).routes, request)(
          total,
          Status.Ok,
        )
    }
  }

  test("Get All Regions") {
    authedReq() { token =>
      GET(
        Uri
          .unsafeFromString("/patient/regions")
          .withQueryParam("x-token", token.value)
      )
    } {
      case request -> security =>
        expectHttpBodyAndStatus(PatientRouters[F](security, patients).routes, request)(
          List(region),
          Status.Ok,
        )
    }
  }

  test("Get Cities by RegionId") {
    authedReq() { token =>
      GET(
        Uri
          .unsafeFromString(s"/patient/cities/${regionIdGen.get}")
          .withQueryParam("x-token", token.value)
      )
    } {
      case request -> security =>
        expectHttpBodyAndStatus(PatientRouters[F](security, patients).routes, request)(
          List(city),
          Status.Ok,
        )
    }
  }
}
