package babymed.test

import cats.effect.IO
import io.circe._
import io.circe.parser.parse
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.headers.`Content-Type`
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.noop.NoOpLogger
import weaver.Expectations
import weaver.SimpleIOSuite
import weaver.scalacheck.Checkers

trait HttpSuite extends SimpleIOSuite with Checkers {
  implicit val logger: SelfAwareStructuredLogger[IO] = NoOpLogger[IO]
  def expectHttpBodyAndStatus[A: Encoder](
      routes: HttpRoutes[IO],
      req: Request[IO],
    )(
      expectedBody: A,
      expectedStatus: Status,
    ): IO[Expectations] =
    routes
      .run(req)
      .cataF(
        IO.pure(failure("route not found 🤔")),
        resp =>
          resp.asJson.map { json =>
            expect.all(
              resp.status == expectedStatus,
              json.dropNullValues == expectedBody.asJson.dropNullValues,
            )
          },
      )

  def expectStreamBodyAndStatus[A: Encoder](
      routes: HttpRoutes[IO],
      req: Request[IO],
    )(
      expectedBody: A,
      expectedStatus: Status,
    ): IO[Expectations] =
    routes
      .run(req)
      .cataF(
        IO.pure(failure("route not found 🤔")),
        resp =>
          resp
            .body
            .through(fs2.text.utf8.decode)
            .map(parse(_).toOption)
            .compile
            .toList
            .map(_.flatten)
            .map { response =>
              expect.all(
                resp.status == expectedStatus,
                response.exists(json => json.dropNullValues == expectedBody.asJson.dropNullValues),
              )
            },
      )

  def expectAsCsvFile(
      routes: HttpRoutes[IO],
      req: Request[IO],
    )(
      expectedStatus: Status
    ): IO[Expectations] =
    routes
      .run(req)
      .cataF(
        IO.pure(failure("route not found 🤔")),
        resp =>
          resp
            .body
            .through(fs2.text.utf8.decode)
            .compile
            .toList
            .map { response =>
              expect.all(
                resp.status == expectedStatus,
                resp.headers.get[`Content-Type`].exists(_.mediaType == MediaType.text.csv),
                response.nonEmpty,
              )
            },
      )

  def expectHttpStatus(
      routes: HttpRoutes[IO],
      req: Request[IO],
    )(
      expectedStatus: Status
    ): IO[Expectations] =
    routes
      .run(req)
      .cata(
        failure("route not found 🤔"),
        resp => expect.same(resp.status, expectedStatus),
      )

  def expectNotFound(routes: HttpRoutes[IO], req: Request[IO]): IO[Expectations] =
    routes
      .run(req)
      .cata(
        success,
        fail("expected a failure 🤨"),
      )

  def expectHttpFailure(routes: HttpRoutes[IO], req: Request[IO]): IO[Expectations] =
    routes.run(req).value.attempt.map {
      case Left(_) => success
      case Right(_) => failure("expected a failure 🤨")
    }
}
