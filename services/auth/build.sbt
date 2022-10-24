import Dependencies.Libraries

name         := "auth"
organization := "baby-med"
scalaVersion := "2.13.10"
scalacOptions += "-language:higherKinds"

lazy val `auth-domain` = project
  .in(file("00-domain"))
  .settings(
    scalacOptions ++= Seq("-Ymacro-annotations"),
    libraryDependencies ++=
      Libraries.Refined.all ++
        Libraries.Derevo.all ++
        Libraries.Circe.all ++
        Seq(
          Libraries.`tsec-pass-hasher`
        ),
  )

lazy val `auth-protocol` =
  project
    .in(file("01-protocol"))
    .dependsOn(`auth-domain`, LocalProject("supports_services"))
    .settings(
      scalacOptions ++= Seq("-Ymacro-annotations"),
      libraryDependencies ++= Seq(
        Libraries.`cats-tagless-macros`
      ),
    )
    .enablePlugins(SrcGenPlugin)

lazy val `auth-core` =
  project
    .in(file("02-core"))
    .settings(
      libraryDependencies ++= Libraries.Logging.all
    )
    .dependsOn(
      `auth-protocol`,
      LocalProject("supports_skunk"),
      LocalProject("test-tools") % Test,
    )

lazy val `auth-server` =
  project
    .in(file("03-server"))
    .dependsOn(`auth-core`)

lazy val `auth-runner` =
  project
    .in(file("04-runner"))
    .dependsOn(`auth-server`)
    .settings(
      libraryDependencies ++= Seq(
        Libraries.GRPC.server
      )
    )
    .settings(DockerImagePlugin.serviceSetting("users"))
    .enablePlugins(DockerImagePlugin, JavaAppPackaging, DockerPlugin)

aggregateProjects(
  `auth-domain`,
  `auth-protocol`,
  `auth-core`,
  `auth-server`,
  `auth-runner`,
)
