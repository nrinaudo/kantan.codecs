package kantan.codecs.laws

import kantan.codecs.DecodeResult
import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class FloatCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder = SimpleDecoder(s ⇒ DecodeResult.nonFatalOr(true)(s.toFloat))
  implicit val encoder = SimpleEncoder[Float](_.toString)

  checkAll("Decoder[String, Float, Boolean]", DecoderTests[String, Float, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Float]", EncoderTests[String, Float, SimpleEncoder].encoder[Int, Int])
}
