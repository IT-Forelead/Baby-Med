import Dependencies.Libraries
import Dependencies.Libraries._

lazy val root = project
  .in(file("."))
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
  .settings(
    name := "common",
    libraryDependencies ++=
      Cats.all ++
        Logging.all ++
        Circe.all ++
        Refined.all ++
        Enumeratum.all ++
        Ciris.all ++
        Derevo.all ++
        Seq(
          Libraries.`monocle-core`,
          Libraries.squants,
        ),
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
    name := "test-tools",
    libraryDependencies ++=
      Libraries.Testing.all ++
        Libraries.Http4s.all ++
        Libraries.Skunk.all,
  )
  .dependsOn(common)

Global / lintUnusedKeysOnLoad := false
Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalafixDependencies += Dependencies.Libraries.`organize-imports`
ThisBuild / scalafixScalaBinaryVersion := scalaBinaryVersion.value
ThisBuild / semanticdbEnabled          := true
ThisBuild / semanticdbVersion          := scalafixSemanticdb.revision
