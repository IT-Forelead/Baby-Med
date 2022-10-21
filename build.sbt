import Dependencies.Libraries._
import Dependencies.Libraries

lazy val projectSettings = Seq(
  version           := "1.0",
  scalaVersion      := "2.13.10",
  organization      := "IT-Forelead",
  scalafmtOnCompile := true,
  scalacOptions ++= CompilerOptions.cOptions,
  Test / compile / coverageEnabled    := true,
  Compile / compile / coverageEnabled := false,
)

lazy val root = project
  .in(file("."))
  .settings(projectSettings: _*)
  .settings(
    name := "baby-med"
  )
  .aggregate(
    integrations,
    supports,
    services,
    `test-tools`,
  )

lazy val common = project
  .in(file("common"))
  .settings(projectSettings: _*)
  .settings(
    name := "common",
    libraryDependencies ++= Cats.all ++ Logging.all ++ Circe.all,
  )

lazy val integrations = project
  .in(file("integrations"))
  .settings(
    name := "integrations"
  )

lazy val supports = project
  .in(file("supports"))
  .settings(
    name := "supports"
  )

lazy val services = project
  .in(file("services"))
  .settings(
    name := "services"
  )

lazy val `test-tools` = project
  .in(file("test"))
  .settings(
    name := "test-tools"
  )
