import Dependencies.Libraries

name         := "skunk"
organization := "IT-Forelead"
scalaVersion := "2.13.8"

libraryDependencies ++= Libraries.Skunk.all

dependsOn(LocalProject("common"))
