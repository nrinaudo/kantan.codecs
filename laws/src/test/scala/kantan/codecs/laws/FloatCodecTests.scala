package kantan.codecs.laws

import kantan.codecs.laws.discipline.{EncoderTests, DecoderTests}
import kantan.codecs.{Encoder, DecodeResult, Decoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class FloatCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val floatDecoder = Decoder[String, Float, Boolean](s ⇒ DecodeResult(s.toFloat).leftMap(_ ⇒ true))
  implicit val floatEncoder = Encoder[String, Float](_.toString)

  checkAll("Decoder[String, Float, Boolean]", DecoderTests[String, Float, Boolean].decoder[Int, Int])
  checkAll("Encoder[String, Float]", EncoderTests[String, Float].encoder[Int, Int])
}
