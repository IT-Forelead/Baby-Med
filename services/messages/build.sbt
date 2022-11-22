import Dependencies.Libraries

name         := "messages"
organization := "baby-med"
scalaVersion := "2.13.10"
scalacOptions += "-language:higherKinds"

lazy val `services_messages-domain` = project
  .in(file("00-domain"))
  .settings(
    scalacOptions ++= Seq("-Ymacro-annotations"),
    libraryDependencies ++=
      Libraries.Refined.all ++
        Libraries.Derevo.all ++
        Libraries.Circe.all ++
        Seq(
          Libraries.newtype
        ),
  )
  .dependsOn(
    LocalProject("common")              % CompileAndTest,
    LocalProject("test-tools")          % CompileAndTest,
    LocalProject("integration_opersms") % CompileAndTest,
  )

lazy val `services_messages-protocol` =
  project
    .in(file("01-protocol"))
    .dependsOn(
      `services_messages-domain`          % CompileAndTest,
      LocalProject("integration_opersms") % CompileAndTest,
      LocalProject("supports_services"),
    )
    .settings(
      scalacOptions ++= Seq("-Ymacro-annotations"),
      libraryDependencies ++= Seq(
        Libraries.`cats-tagless-macros`
      ),
    )
    .enablePlugins(SrcGenPlugin)

lazy val `services_messages-core` =
  project
    .in(file("02-core"))
    .settings(
      libraryDependencies ++= Libraries.Logging.all
    )
    .dependsOn(
      `services_messages-protocol`        % CompileAndTest,
      LocalProject("support_database")    % CompileAndTest,
      LocalProject("migrations")          % CompileAndTest,
      LocalProject("integration_opersms") % CompileAndTest,
    )

lazy val `services_messages-server` =
  project
    .in(file("03-server"))
    .dependsOn(`services_messages-core`)

lazy val `services_messages-runner` =
  project
    .in(file("04-runner"))
    .dependsOn(`services_messages-server`, LocalProject("support_database"))
    .settings(
      libraryDependencies ++= Seq(
        Libraries.GRPC.server
      )
    )
    .settings(DockerImagePlugin.serviceSetting("messages"))
    .enablePlugins(DockerImagePlugin, JavaAppPackaging, DockerPlugin)

aggregateProjects(
  `services_messages-domain`,
  `services_messages-protocol`,
  `services_messages-core`,
  `services_messages-server`,
  `services_messages-runner`,
)
