import Dependencies.Libraries

name         := "services"
organization := "IT-Forelead"
scalaVersion := "2.13.10"

scalacOptions ++= Seq(
  "-language:experimental.macros",
  "-Ymacro-annotations",
  "-language:higherKinds",
)
libraryDependencies ++=
  Libraries.GRPC.all ++
    Libraries.MEOW.all ++
    Libraries.Http4s.all ++
    Seq(
      Libraries.Logging.log4cats,
      Libraries.izumi,
    )

dependsOn(LocalProject("common"))
