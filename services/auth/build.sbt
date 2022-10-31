import Dependencies.Libraries

name         := "auth"
organization := "babymed"
scalaVersion := "2.13.10"
scalacOptions ++= Seq("-language:higherKinds", "-Ymacro-annotations")

libraryDependencies ++=
  Libraries.Derevo.all ++
    Seq(
      Libraries.`http4s-jwt-auth`,
      Libraries.newtype,
    )

dependsOn(
  LocalProject("common"),
  LocalProject("services_users-protocol"),
  LocalProject("supports_redis"),
  LocalProject("test-tools") % Test,
)
