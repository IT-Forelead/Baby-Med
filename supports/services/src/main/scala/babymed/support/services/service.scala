package babymed.support.services

import babymed.support.services.internal.ServiceDerivationMacro
import higherkindness.mu.rpc.protocol._

import scala.annotation._
import scala.language.experimental.macros

@compileTimeOnly("macros paradise must be enabled")
class service(
    val serializationType: SerializationType,
    val compressionType: CompressionType = Identity,
    val namespace: Option[String] = None,
    val methodNameStyle: MethodNameStyle = Unchanged,
  ) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ServiceDerivationMacro.deriveService
}
