package scalograf
package model

import io.circe.CursorOp.DownField
import io.circe.Decoder.Result
import io.circe.{Codec, Decoder, DecodingFailure, Encoder, HCursor, Json}
import io.circe.syntax._
import cats.syntax.functor._

sealed trait Color

object Color {
  case class RGBa(red: Short, green: Short, blue: Short, alpha: Short = 255) extends Color
  case class Named(name: String) extends Color //ToDo full enum???

  implicit val colorEncoder = Encoder.instance[Color] {
    case RGBa(red, green, blue, alpha) => s"rgba($red, $green, $blue, $alpha)".asJson
    case Named(name)                   => name.asJson
  }

  private implicit val rgbaDecoder = Decoder.instance[RGBa] { cursor =>
    val regex = "^rgba\\((\\d+),\\s*(\\d+),\\s*(\\d+),\\s*(\\d+)\\)$".r
    cursor.value.asString match {
      case Some(regex(red, green, blue, alpha)) => Right(RGBa(red.toShort, green.toShort, blue.toShort, alpha.toShort))
      case Some(_)                              => Left(DecodingFailure("rgba color string have wrong format", cursor.history))
      case _                                    => Left(DecodingFailure("rgba color must be a string", cursor.history))

    }
  }

  implicit val colorDecoder =
    List[Decoder[Color]](
      rgbaDecoder.widen,
      implicitly[Decoder[String]].map(Named.apply)
    ).reduce(_ or _)
}