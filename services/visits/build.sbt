import Dependencies.Libraries

name         := "visits"
organization := "baby-med"
scalaVersion := "2.13.10"
scalacOptions += "-language:higherKinds"

lazy val `services_visits-domain` = project
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

lazy val `services_visits-protocol` =
  project
    .in(file("01-protocol"))
    .dependsOn(
      `services_visits-domain` % CompileAndTest,
      LocalProject("supports_services"),
    )
    .settings(
      scalacOptions ++= Seq("-Ymacro-annotations"),
      libraryDependencies ++= Seq(
        Libraries.`cats-tagless-macros`
      ),
    )
    .enablePlugins(SrcGenPlugin)

lazy val `services_visits-core` =
  project
    .in(file("02-core"))
    .settings(
      libraryDependencies ++= Libraries.Logging.all
    )
    .dependsOn(
      `services_visits-protocol`          % CompileAndTest,
      LocalProject("support_database")    % CompileAndTest,
      LocalProject("migrations")          % CompileAndTest,
      LocalProject("services_users-core") % Test,
    )

lazy val `services_visits-server` =
  project
    .in(file("03-server"))
    .dependsOn(`services_visits-core`)

lazy val `services_visits-runner` =
  project
    .in(file("04-runner"))
    .dependsOn(`services_visits-server`, LocalProject("support_database"))
    .settings(
      libraryDependencies ++= Seq(
        Libraries.GRPC.server
      )
    )
    .settings(DockerImagePlugin.serviceSetting("visits"))
    .enablePlugins(DockerImagePlugin, JavaAppPackaging, DockerPlugin)

aggregateProjects(
  `services_visits-domain`,
  `services_visits-protocol`,
  `services_visits-core`,
  `services_visits-server`,
  `services_visits-runner`,
)
