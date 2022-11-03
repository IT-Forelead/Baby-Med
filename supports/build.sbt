name         := "supports"
scalaVersion := "2.13.10"

lazy val supports_sttp = project.in(file("sttp"))
lazy val supports_redis = project.in(file("redis"))
lazy val supports_services = project.in(file("services"))
lazy val supports_skunk = project.in(file("skunk"))
lazy val supports_mailer = project.in(file("mailer"))
lazy val support_database = project in file("database")

aggregateProjects(
  supports_sttp,
  supports_redis,
  supports_services,
  supports_skunk,
  supports_mailer,
  support_database,
)
