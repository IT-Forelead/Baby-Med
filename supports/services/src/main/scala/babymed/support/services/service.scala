package babymed.support.services

import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

import babymed.support.services.internal.ServiceDerivationMacro
import higherkindness.mu.rpc.protocol._

// $COVERAGE-OFF$
@compileTimeOnly("macros paradise must be enabled")
class service(
    val serializationType: SerializationType,
    val compressionType: CompressionType = Identity,
    val namespace: Option[String] = None,
    val methodNameStyle: MethodNameStyle = Unchanged,
  ) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ServiceDerivationMacro.deriveService
}
// $COVERAGE-ON$
