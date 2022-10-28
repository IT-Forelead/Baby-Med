import sbt.ModuleID
import sbt._

object Dependencies {
  object Versions {
    lazy val cats = "2.7.0"
    lazy val `cats-effect` = "3.3.5"
    lazy val circe = "0.14.1"
    lazy val fs2 = "3.2.4"
    lazy val http4s = "0.23.10"
    lazy val log4cats = "2.5.0"
    lazy val skunk = "0.2.3"
    lazy val logback = "1.2.10"
    lazy val ciris = "2.3.2"
    lazy val refined = "0.10.1"
    lazy val tsec = "0.4.0"
    lazy val redis4cats = "1.1.1"
    lazy val `cats-retry` = "3.1.0"
    lazy val newtype = "0.4.4"
    lazy val derevo = "0.13.0"
    lazy val sttp = "3.7.2"
    lazy val monocle = "3.1.0"
    lazy val `cats-tagless` = "0.14.0"
    lazy val `mu-rpc` = "0.29.0"
    lazy val `http4s-jwt-auth` = "1.0.0"
    lazy val `meow-mtl` = "0.5.0"
    lazy val mailer = "1.4.7"
    lazy val izumi = "2.2.0"
    lazy val enumeratum = "1.7.0"

    lazy val weaver = "0.8.0"
    lazy val `test-container` = "1.17.3"
    lazy val postgresql = "42.5.0"
  }

  trait LibGroup {
    def all: Seq[ModuleID]
  }

  object Libraries {
    object Circe extends LibGroup {
      private def circe(artifact: String): ModuleID =
        "io.circe" %% s"circe-$artifact" % Versions.circe

      lazy val core: ModuleID = circe("core")
      lazy val generic: ModuleID = circe("generic")
      lazy val parser: ModuleID = circe("parser")
      lazy val refined: ModuleID = circe("refined")
      override def all: Seq[ModuleID] = Seq(core, generic, parser, refined)
    }

    object Skunk extends LibGroup {
      private def skunk(artifact: String): ModuleID =
        "org.tpolecat" %% artifact % Versions.skunk

      lazy val core: ModuleID = skunk("skunk-core")
      lazy val circe: ModuleID = skunk("skunk-circe")
      lazy val refined: ModuleID = skunk("refined")
      override def all: Seq[ModuleID] = Seq(core, circe, refined)
    }

    object Ciris extends LibGroup {
      private def ciris(artifact: String): ModuleID =
        "is.cir" %% artifact % Versions.ciris

      lazy val core: ModuleID = ciris("ciris")
      lazy val enum: ModuleID = ciris("ciris-enumeratum")
      lazy val refined: ModuleID = ciris("ciris-refined")
      override def all: Seq[ModuleID] = Seq(core, enum, refined)
    }

    object Derevo extends LibGroup {
      private def derevo(artifact: String): ModuleID =
        "tf.tofu" %% s"derevo-$artifact" % Versions.derevo

      lazy val core: ModuleID = derevo("core")
      lazy val cats: ModuleID = derevo("cats")
      lazy val circe: ModuleID = derevo("circe-magnolia")
      override def all: Seq[ModuleID] = Seq(core, cats, circe)
    }

    object Http4s extends LibGroup {
      private def http4s(artifact: String): ModuleID =
        "org.http4s" %% s"http4s-$artifact" % Versions.http4s

      lazy val dsl: ModuleID = http4s("dsl")
      lazy val server: ModuleID = http4s("ember-server")
      lazy val client: ModuleID = http4s("ember-client")
      lazy val circe: ModuleID = http4s("circe")
      lazy val `blaze-server`: ModuleID = http4s("blaze-server")
      override def all: Seq[ModuleID] = Seq(dsl, server, client, circe)
    }

    object Refined extends LibGroup {
      private def refined(artifact: String): ModuleID =
        "eu.timepit" %% artifact % Versions.refined

      lazy val core: ModuleID = refined("refined")
      lazy val cats: ModuleID = refined("refined-cats")
      override def all: Seq[ModuleID] = Seq(core, cats)
    }

    object Redis extends LibGroup {
      private def redis4cats(artifact: String): ModuleID =
        "dev.profunktor" %% artifact % Versions.redis4cats

      lazy val catsEffects: ModuleID = redis4cats("redis4cats-effects")
      lazy val log4cats: ModuleID = redis4cats("redis4cats-log4cats")
      override def all: Seq[ModuleID] = Seq(catsEffects, log4cats)
    }

    object Cats extends LibGroup {
      lazy val retry = "com.github.cb372" %% "cats-retry"  % Versions.`cats-retry`
      lazy val core = "org.typelevel"     %% "cats-core"   % Versions.cats
      lazy val effect = "org.typelevel"   %% "cats-effect" % Versions.`cats-effect`
      def all: Seq[ModuleID] = Seq(core, retry, effect)
    }

    object Logging extends LibGroup {
      lazy val log4cats = "org.typelevel" %% "log4cats-slf4j"  % Versions.log4cats
      lazy val logback = "ch.qos.logback"  % "logback-classic" % Versions.logback
      override def all: Seq[ModuleID] = Seq(log4cats, logback)
    }

    object Sttp extends LibGroup {
      private def sttp(artifact: String): ModuleID =
        "com.softwaremill.sttp.client3" %% artifact % Versions.sttp

      lazy val circe: ModuleID = sttp("circe")
      lazy val `fs2-backend`: ModuleID = sttp("async-http-client-backend-fs2")
      override def all: Seq[ModuleID] = Seq(circe, `fs2-backend`)
    }

    object GRPC extends LibGroup {
      private def muRpc(artifact: String): ModuleID =
        "io.higherkindness" %% artifact % Versions.`mu-rpc`

      lazy val service: ModuleID = muRpc("mu-rpc-service")
      lazy val server: ModuleID = muRpc("mu-rpc-server")
      lazy val fs2: ModuleID = muRpc("mu-rpc-fs2")
      override def all: Seq[ModuleID] = Seq(service, server, fs2)
    }

    object MEOW extends LibGroup {
      private def meowMtl(artifact: String): ModuleID =
        "com.olegpy" %% artifact % Versions.`meow-mtl`

      lazy val core: ModuleID = meowMtl("meow-mtl-core")
      override def all: Seq[ModuleID] = Seq(core)
    }

    object Enumeratum extends LibGroup {
      private def enumeratum(artifact: String): ModuleID =
        "com.beachape" %% artifact % Versions.enumeratum
      lazy val core: ModuleID = enumeratum("enumeratum")
      lazy val circe: ModuleID = enumeratum("enumeratum-circe")
      lazy val cats: ModuleID = enumeratum("enumeratum-cats")
      override def all: Seq[ModuleID] = Seq(core, circe, cats)
    }

    object Testing extends LibGroup {
      lazy val `log4cats-noop` = "org.typelevel"     %% "log4cats-noop"      % Versions.log4cats
      lazy val `refined-scalacheck` = "eu.timepit"   %% "refined-scalacheck" % Versions.refined
      lazy val `weaver-cats` = "com.disneystreaming" %% "weaver-cats"        % Versions.weaver
      lazy val `weaver-discipline` = "com.disneystreaming"  %% "weaver-discipline" % Versions.weaver
      lazy val `weaver-scala-check` = "com.disneystreaming" %% "weaver-scalacheck" % Versions.weaver
      lazy val `test-container` = "org.testcontainers" % "postgresql" % Versions.`test-container`
      lazy val postgresql = "org.postgresql"           % "postgresql" % Versions.postgresql
      override def all: Seq[ModuleID] = Seq(
        `log4cats-noop`,
        `refined-scalacheck`,
        `weaver-cats`,
        `weaver-discipline`,
        `weaver-scala-check`,
        `test-container`,
        postgresql,
      )
    }
    lazy val fs2 = "co.fs2"                            %% "fs2-core"      % Versions.fs2
    lazy val newtype = "io.estatico"                   %% "newtype"       % Versions.newtype
    lazy val `tsec-pass-hasher` = "io.github.jmcardon" %% "tsec-password" % Versions.tsec
    lazy val `monocle-core` = "dev.optics"             %% "monocle-core"  % Versions.monocle
    lazy val `http4s-jwt-auth` = "dev.profunktor" %% "http4s-jwt-auth" % Versions.`http4s-jwt-auth`
    lazy val mailer = "javax.mail"                 % "mail"            % Versions.mailer
    lazy val izumi = "dev.zio"                    %% "izumi-reflect"   % Versions.izumi

    lazy val `cats-tagless-macros` =
      "org.typelevel" %% "cats-tagless-macros" % Versions.`cats-tagless`
  }
}
