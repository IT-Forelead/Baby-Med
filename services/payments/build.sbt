import Dependencies.Libraries

name         := "payments"
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
          Libraries.newtype,
        ),
  )
  .dependsOn(
    LocalProject("common")                % CompileAndTest,
    LocalProject("services_users-domain") % CompileAndTest,
    LocalProject("test-tools")            % CompileAndTest,
  )

lazy val `services_payments-protocol` =
  project
    .in(file("01-protocol"))
    .dependsOn(`services_payments-domain` % CompileAndTest, LocalProject("supports_services"))
    .settings(
      scalacOptions ++= Seq("-Ymacro-annotations"),
      libraryDependencies ++= Seq(
        Libraries.`cats-tagless-macros`
      ),
    )
    .enablePlugins(SrcGenPlugin)

lazy val `services_payments-core` =
  project
    .in(file("02-core"))
    .settings(
      libraryDependencies ++= Libraries.Logging.all
    )
    .dependsOn(
      `services_payments-protocol`        % CompileAndTest,
      LocalProject("support_database")    % CompileAndTest,
      LocalProject("migrations")          % CompileAndTest,
      LocalProject("services_users-core") % Test,
    )

lazy val `services_payments-server` =
  project
    .in(file("03-server"))
    .dependsOn(`services_payments-core`)

lazy val `services_payments-runner` =
  project
    .in(file("04-runner"))
    .dependsOn(`services_payments-server`, LocalProject("support_database"))
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
  `services_payments-runner`,
)
