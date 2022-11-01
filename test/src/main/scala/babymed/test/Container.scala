package babymed.test

import scala.io.Source

import cats.effect.IO
import cats.effect.Resource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.noop.NoOpLogger
import weaver.scalacheck.CheckConfig

trait Container {
  type Res
  lazy val imageName: String = "postgres:12"
  lazy val container: PostgreSQLContainer[Nothing] = new PostgreSQLContainer(
    DockerImageName
      .parse(imageName)
      .asCompatibleSubstituteFor("postgres")
  )

  val customCheckConfig: CheckConfig = CheckConfig.default.copy(minimumSuccessful = 20)

  implicit val logger: SelfAwareStructuredLogger[IO] = NoOpLogger[IO]
  //  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def migrateSql(container: PostgreSQLContainer[Nothing]): Unit = {
    val source = Source.fromFile(getClass.getResource("/tables.sql").getFile)
    val sqlScripts = source.getLines().mkString
    source.close()
    val connection = container.createConnection(
      s"?user=${container.getUsername}&password=${container.getPassword}"
    )
    val stmt = connection.createStatement()
    stmt.execute(sqlScripts)
    stmt.closeOnCompletion()
  }

  val dbResource: Resource[IO, PostgreSQLContainer[Nothing]] =
    for {
      container <- Resource.fromAutoCloseable {
        IO {
          container.setCommand("postgres", "-c", "max_connections=150")
          container.start()
          container
        }
      }
      _ = migrateSql(container)
      _ <- Resource.eval(logger.info("Container has started"))
    } yield container
}
