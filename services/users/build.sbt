import Dependencies.Libraries

name         := "users"
organization := "baby-med"
scalaVersion := "2.13.10"
scalacOptions += "-language:higherKinds"

lazy val `services_users-domain` = project
  .in(file("00-domain"))
  .settings(
    scalacOptions ++= Seq("-Ymacro-annotations"),
    libraryDependencies ++=
      Libraries.Refined.all ++
        Libraries.Derevo.all ++
        Libraries.Circe.all ++
        Seq(
          Libraries.`tsec-pass-hasher`
        )
  )
  .dependsOn(LocalProject("common"))

lazy val `services_users-protocol` =
  project
    .in(file("01-protocol"))
    .dependsOn(`services_users-domain`, LocalProject("supports_services"))
    .settings(
      scalacOptions ++= Seq("-Ymacro-annotations"),
      libraryDependencies ++= Seq(
        Libraries.`cats-tagless-macros`
      )
    )
    .enablePlugins(SrcGenPlugin)

lazy val `services_users-core` =
  project
    .in(file("02-core"))
    .settings(
      libraryDependencies ++= Libraries.Logging.all
    )
    .dependsOn(
      `services_users-protocol`,
      LocalProject("test-tools") % Test
    )

lazy val `services_users-server` =
  project
    .in(file("03-server"))
    .dependsOn(`services_users-core`)

lazy val `services_users-runner` =
  project
    .in(file("04-runner"))
    .dependsOn(`services_users-server`)
    .settings(
      libraryDependencies ++= Seq(
        Libraries.GRPC.server
      )
    )
    .settings(DockerImagePlugin.serviceSetting("users"))
    .enablePlugins(DockerImagePlugin, JavaAppPackaging, DockerPlugin)

aggregateProjects(
  `services_users-domain`,
  `services_users-protocol`,
  `services_users-core`,
  `services_users-server`,
  `services_users-runner`
)
