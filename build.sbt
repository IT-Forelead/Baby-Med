import Dependencies.Libraries
import Dependencies.Libraries._

lazy val `baby-med` = project
  .in(file("."))
  .settings(name := "baby-med")
  .aggregate(
    migrations,
    integrations,
    supports,
    services,
    `test-tools`
  )

addCommandAlias(
  "styleCheck", // TODO formatAndCheck - "all scalafmtSbtCheck; scalafmtCheckAll; test; scalafixAll --check"
  "all scalafmtSbtCheck; scalafmtCheckAll; Test / compile; scalafixAll --check"
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
        Seq(Libraries.`monocle-core`, Libraries.squants) // replace `monocle-core` with monocleCore
  )
lazy val migrations =
  project.in(file("migrations"))

lazy val integrations = project
  .in(file("integrations"))
  .settings(name := "integrations")

lazy val supports = project
  .in(file("supports"))
  .settings(name := "supports")

lazy val services = project
  .in(file("services"))
  .settings(name := "services")

lazy val `test-tools` = project
  .in(file("test"))
  .settings(
    name := "test-tools",
    libraryDependencies ++=
      Libraries.Testing.all ++
        Libraries.Http4s.all ++
        Libraries.Skunk.all // replace all with Libraries.testingDependencies
  )
  .dependsOn(common)

Global / lintUnusedKeysOnLoad := false
Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalafixDependencies += Dependencies.Libraries.`organize-imports`
ThisBuild / scalafixScalaBinaryVersion := scalaBinaryVersion.value
ThisBuild / semanticdbEnabled          := true
ThisBuild / semanticdbVersion          := scalafixSemanticdb.revision
