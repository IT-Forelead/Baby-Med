name         := "services"
organization := "baby-med"
scalaVersion := "2.13.10"

lazy val services_auth = project.in(file("auth"))
lazy val services_users = project.in(file("users"))
lazy val services_payments = project.in(file("payments"))
lazy val services_migrations = project.in(file("migrations"))

aggregateProjects(
  services_auth,
  services_users,
  services_payments,
  services_migrations,
)
