import Dependencies.Libraries

name         := "sttp"
organization := "IT-Forelead"
scalaVersion := "2.13.10"

libraryDependencies ++= Libraries.Sttp.all ++ Libraries.Logging.all
