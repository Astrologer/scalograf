package scalograf
package model.panels

import io.circe.JsonObject
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class Transformation(id: Option[String] = None, options: JsonObject = JsonObject())

object Transformation {
  implicit val config = Configuration.default
  implicit val codec = deriveConfiguredCodec[Transformation]
}
