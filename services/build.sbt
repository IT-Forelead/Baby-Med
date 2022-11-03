name         := "services"
organization := "baby-med"
scalaVersion := "2.13.10"

lazy val services_auth = project.in(file("auth"))
lazy val services_users = project.in(file("users"))
lazy val services_payments = project.in(file("payments"))
lazy val `services_babymed-api` = project.in(file("babymed-api"))

aggregateProjects(
  services_auth,
  services_users,
  services_payments,
  `services_babymed-api`
)
