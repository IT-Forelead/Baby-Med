import Dependencies.Libraries

name         := "database"
organization := "IT-Forelead"
scalaVersion := "2.13.10"

dependsOn(LocalProject("common"))
libraryDependencies ++=
  Seq(
    Libraries.Testing.postgresql,
    Libraries.Flyway.`flyway-core`,
  )
