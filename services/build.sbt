name         := "services"
organization := "baby-med"
scalaVersion := "2.13.10"

lazy val services_auth = project.in(file("auth"))
lazy val services_users = project.in(file("users"))
lazy val services_visits = project.in(file("visits"))
lazy val services_messages = project.in(file("messages"))
lazy val `services_babymed-api` = project.in(file("babymed-api"))

aggregateProjects(
  services_auth,
  services_users,
  services_visits,
  services_messages,
  `services_babymed-api`,
)
