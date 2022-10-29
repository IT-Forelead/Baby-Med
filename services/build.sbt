name         := "services"
organization := "baby-med"
scalaVersion := "2.13.10"

lazy val services_auth = project.in(file("auth"))
lazy val services_users = project.in(file("users"))

aggregateProjects(
  services_auth,
  services_users,
)
