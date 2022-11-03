import Dependencies.Libraries

name         := "babymed-api"
organization := "IT-Forelead"
scalaVersion := "2.13.10"
scalacOptions += "-language:higherKinds"

lazy val `services_babymed-api-core` =
  project
    .in(file("00-core"))
    .dependsOn(
      LocalProject("supports_services"),
      LocalProject("services_users-protocol"),
      LocalProject("services_payments-protocol"),
      LocalProject("services_auth"),
    )
    .settings(
      libraryDependencies ++= Libraries.Logging.all
    )

lazy val `services_babymed-api-server` =
  project
    .in(file("01-server"))
    .dependsOn(`services_babymed-api-core`)

lazy val `services_babymed-api-runner` =
  project
    .in(file("02-runner"))
    .dependsOn(`services_babymed-api-server`)
    .settings(DockerImagePlugin.serviceSetting("babymed-api"))
    .enablePlugins(DockerImagePlugin, JavaAppPackaging, DockerPlugin)

aggregateProjects(
  `services_babymed-api-core`,
  `services_babymed-api-server`,
  `services_babymed-api-runner`,
)
