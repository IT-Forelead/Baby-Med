import Dependencies.Libraries

name         := "baby-med"
organization := "cieloassist"
scalaVersion := "2.13.10"
scalacOptions += "-language:higherKinds"

lazy val `services_baby-med-domain` = project
  .in(file("00-domain"))
  .dependsOn(
    LocalProject("test-tools"),
  )
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

lazy val `services_baby-med-protocol` =
  project
    .in(file("01-protocol"))
    .dependsOn(
      `services_baby-med-domain`
    )
    .settings(
      scalacOptions ++= Seq("-Ymacro-annotations"),
      libraryDependencies ++= Seq(
        Libraries.`cats-tagless-macros`
      ),
    )
    .enablePlugins(SrcGenPlugin)

lazy val `services_baby-med-core` =
  project
    .in(file("02-core"))
    .dependsOn(
      `services_baby-med-protocol`,
      LocalProject("supports_skunk"),
      LocalProject("supports_mailer"),
    )
    .settings(
      libraryDependencies ++= Libraries.Logging.all
    )

lazy val `services_baby-med-server` =
  project
    .in(file("03-server"))
    .dependsOn(`services_baby-med-core`)

lazy val `services_baby-med-runner` =
  project
    .in(file("04-runner"))
    .dependsOn(`services_baby-med-server`)
    .settings(
      libraryDependencies ++= Seq(
        Libraries.GRPC.server
      )
    )
    .settings(DockerImagePlugin.serviceSetting("baby-med"))
    .enablePlugins(DockerImagePlugin, JavaAppPackaging, DockerPlugin)

aggregateProjects(
  `services_baby-med-domain`,
  `services_baby-med-protocol`,
  `services_baby-med-core`,
  `services_baby-med-server`,
  `services_baby-med-runner`,
)
