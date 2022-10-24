import Dependencies.Libraries

name         := "redis"
organization := "IT-Forelead"
scalaVersion := "2.13.10"

libraryDependencies ++= Libraries.Redis.all

dependsOn(LocalProject("common"))
