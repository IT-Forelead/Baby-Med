resolvers ++= Seq(
  Resolver.url("jetbrains-bintray", url("https://dl.bintray.com/jetbrains/sbt-plugins/"))(
    Resolver.ivyStylePatterns
  )
)

addSbtPlugin("io.higherkindness" %% "sbt-mu-srcgen"       % "0.29.0")
addSbtPlugin("com.typesafe.sbt"   % "sbt-native-packager" % "1.8.1")
addSbtPlugin("io.spray"           % "sbt-revolver"        % "0.9.1")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"       % "1.9.3")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"        % "2.4.6")
addSbtPlugin("ch.epfl.scala"      % "sbt-scalafix"        % "0.10.4")
