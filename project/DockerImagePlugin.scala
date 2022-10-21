import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.docker.{DockerChmodType, DockerPlugin}
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.{Docker, dockerChmodType}
import sbt.Keys._
import sbt.{Def, _}

object DockerImagePlugin extends AutoPlugin {
  object autoImport {
    lazy val generateServiceImage: TaskKey[Unit] =
      taskKey[Unit]("Generates an image with the native binary")
  }
  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      dockerBaseImage    := "babymed-base-server:1.0",
      dockerUpdateLatest := true,
      dockerChmodType    := DockerChmodType.UserGroupWriteExecute,
    )

  def serviceSetting(serviceName: String): Seq[Def.Setting[_]] =
    Seq(
      Docker / packageName         := s"babymed-$serviceName",
      packageDoc / publishArtifact := false,
      packageSrc / publishArtifact := true,
    )

  override def requires: sbt.Plugins =
    JavaAppPackaging && DockerPlugin
}
