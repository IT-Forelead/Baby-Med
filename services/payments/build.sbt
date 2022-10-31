import Dependencies.Libraries

name := "users"
organization := "baby-med"
scalaVersion := "2.13.10"
scalacOptions += "-language:higherKinds"

lazy val `services_payments-domain` = project
  .in(file("00-domain"))
  .settings(
    scalacOptions ++= Seq("-Ymacro-annotations"),
    libraryDependencies ++=
      Libraries.Refined.all ++
        Libraries.Derevo.all ++
        Libraries.Circe.all ++
        Seq(
          Libraries.`tsec-pass-hasher`,
          Libraries.newtype
        )
  )
  .dependsOn(
    LocalProject("common"),
    LocalProject("services_users-domain"),
    LocalProject("test-tools")
  )

lazy val `services_payments-protocol` =
  project
    .in(file("01-protocol"))
    .dependsOn(`services_payments-domain`, LocalProject("supports_services"))
    .settings(
      scalacOptions ++= Seq("-Ymacro-annotations"),
      libraryDependencies ++= Seq(
        Libraries.`cats-tagless-macros`
      )
    )
    .enablePlugins(SrcGenPlugin)

lazy val `services_payments-core` =
  project
    .in(file("02-core"))
    .settings(
      libraryDependencies ++= Libraries.Logging.all
    )
    .dependsOn(
      `services_payments-protocol`,
      LocalProject("supports_skunk"),
      LocalProject("test-tools") % Test
    )

lazy val `services_payments-server` =
  project
    .in(file("03-server"))
    .dependsOn(`services_payments-core`)

lazy val `services_payments-runner` =
  project
    .in(file("04-runner"))
    .dependsOn(`services_payments-server`)
    .settings(
      libraryDependencies ++= Seq(
        Libraries.GRPC.server
      )
    )
    .settings(DockerImagePlugin.serviceSetting("payments"))
    .enablePlugins(DockerImagePlugin, JavaAppPackaging, DockerPlugin)

aggregateProjects(
  `services_payments-domain`,
  `services_payments-protocol`,
  `services_payments-core`,
  `services_payments-server`,
  `services_payments-runner`
)
