package babymed.support.services

package object syntax {
  object all extends Http4sSyntax with MarshallerSyntax
  object marshaller extends MarshallerSyntax
  object http4s extends Http4sSyntax

}
