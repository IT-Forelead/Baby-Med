name         := "integrations"
scalaVersion := "2.13.10"

lazy val integration_opersms = project.in(file("opersms"))

aggregateProjects(
  integration_opersms
)
