import Dependencies.Libraries

name         := "mailer"
organization := "IT-Forelead"
scalaVersion := "2.13.10"

scalacOptions ++= Seq("-Ymacro-annotations")
libraryDependencies ++= Libraries.Cats.all ++ Libraries.Refined.all ++ Seq(
  Libraries.mailer,
  Libraries.newtype,
  Libraries.Logging.log4cats,
)

dependsOn(LocalProject("common"))
