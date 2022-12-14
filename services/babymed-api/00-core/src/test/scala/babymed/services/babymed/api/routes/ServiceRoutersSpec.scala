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
import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.EditService
import babymed.services.visits.domain.Service
import babymed.services.visits.domain.ServiceType
import babymed.services.visits.domain.ServiceWithTypeName
import babymed.services.visits.domain.types.ServiceId
import babymed.services.visits.domain.types.ServiceTypeId
import babymed.services.visits.domain.types.ServiceTypeName
import babymed.services.visits.generators.ServiceGenerators
import babymed.services.visits.proto.Services
import babymed.support.redis.RedisClientMock
import babymed.support.services.syntax.all._
import babymed.syntax.refined.commonSyntaxAutoUnwrapV
import babymed.test.HttpSuite

object ServiceRoutersSpec extends HttpSuite with ServiceGenerators with UserGenerators {
  val jwtConfig: JwtConfig =
    JwtConfig(
      Secret(JwtAccessTokenKey(NonEmptyString.unsafeFrom("test"))),
      TokenExpiration(1.minutes),
    )

  lazy val user: User = userGen.get
  lazy val credentials: Credentials =
    Credentials(phoneGen.get, NonEmptyString.unsafeFrom(nonEmptyStringGen(8).get))
  lazy val service: Service = serviceGen.get
  lazy val serviceType: ServiceType = serviceTypeGen.get
  lazy val serviceWithTypeName: ServiceWithTypeName = serviceWithTypeNameGen.get

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

  val services: Services[F] = new Services[F] {
    override def create(createService: CreateService): F[Service] =
      Sync[F].delay(service)
    override def getServicesByTypeId(serviceTypeId: ServiceTypeId): F[List[Service]] =
      Sync[F].delay(List(service))
    override def get: F[List[ServiceWithTypeName]] =
      Sync[F].delay(List(serviceWithTypeName))
    override def edit(editService: EditService): F[Unit] = Sync[F].unit
    override def delete(serviceId: ServiceId): F[Unit] = Sync[F].unit
    override def createServiceType(name: ServiceTypeName): F[ServiceType] =
      Sync[F].delay(serviceType)
    override def getServiceTypes: F[List[ServiceType]] =
      Sync[F].delay(List(serviceType))
    override def deleteServiceType(id: ServiceTypeId): F[Unit] = Sync[F].unit
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

  test("Create service with incorrect role") {
    authedReq(Doctor) { token =>
      POST(createServiceGen().get, uri"/service/create").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectNotFound(ServiceRouters[F](security, services).routes, request)
    }
  }

  test("Create service with correct role") {
    authedReq() { token =>
      POST(createServiceGen().get, uri"/service/create").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectHttpStatus(ServiceRouters[F](security, services).routes, request)(Status.NoContent)
    }
  }

  test("Get Services by TypeId") {
    val serviceTypeId = serviceTypeIdGen.get
    authedReq() { token =>
      GET(Uri.unsafeFromString(s"/service/services?type_id=$serviceTypeId"))
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpBodyAndStatus(ServiceRouters[F](security, services).routes, request)(
          List(service),
          Status.Ok,
        )
    }
  }

  test("Get All Services") {
    authedReq() { token =>
      GET(uri"/service/services")
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpBodyAndStatus(ServiceRouters[F](security, services).routes, request)(
          List(serviceWithTypeName),
          Status.Ok,
        )
    }
  }

  test("Edit service with incorrect role") {
    authedReq(Doctor) { token =>
      POST(editServiceGen().get, uri"/service/edit").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectNotFound(ServiceRouters[F](security, services).routes, request)
    }
  }

  test("Edit service with correct role") {
    authedReq() { token =>
      POST(editServiceGen().get, uri"/service/edit").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectHttpStatus(ServiceRouters[F](security, services).routes, request)(Status.NoContent)
    }
  }

  test("Delete service with incorrect role") {
    authedReq(Doctor) { token =>
      val serviceId = serviceGen.get.id
      GET(Uri.unsafeFromString(s"/service/delete/$serviceId")).bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectNotFound(ServiceRouters[F](security, services).routes, request)
    }
  }

  test("Delete service with correct role") {
    authedReq() { token =>
      val serviceId = serviceGen.get.id
      GET(Uri.unsafeFromString(s"/service/delete/$serviceId")).bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectHttpStatus(ServiceRouters[F](security, services).routes, request)(Status.NoContent)
    }
  }

  test("Create Service Type with incorrect role") {
    authedReq(Doctor) { token =>
      POST(serviceTypeNameGen.get, uri"/service/create/service-type").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectNotFound(ServiceRouters[F](security, services).routes, request)
    }
  }

  test("Create Service Type with correct role") {
    authedReq() { token =>
      POST(serviceTypeNameGen.get, uri"/service/create/service-type").bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectHttpStatus(ServiceRouters[F](security, services).routes, request)(Status.NoContent)
    }
  }

  test("Get Service Types") {
    authedReq() { token =>
      GET(uri"/service/service-types")
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpBodyAndStatus(ServiceRouters[F](security, services).routes, request)(
          List(serviceType),
          Status.Ok,
        )
    }
  }

  test("Delete Service Type with incorrect role") {
    authedReq(Doctor) { token =>
      val serviceTypeId = serviceTypeGen.get.id
      GET(Uri.unsafeFromString(s"/service/delete-service-type/$serviceTypeId")).bearer(
        NonEmptyString.unsafeFrom(token.value)
      )
    } {
      case request -> security =>
        expectNotFound(ServiceRouters[F](security, services).routes, request)
    }
  }

  test("Delete Service Type with correct role") {
    authedReq() { token =>
      val serviceTypeId = serviceTypeGen.get.id
      GET(Uri.unsafeFromString(s"/service/delete-service-type/$serviceTypeId"))
        .bearer(NonEmptyString.unsafeFrom(token.value))
    } {
      case request -> security =>
        expectHttpStatus(ServiceRouters[F](security, services).routes, request)(Status.NoContent)
    }
  }
}
