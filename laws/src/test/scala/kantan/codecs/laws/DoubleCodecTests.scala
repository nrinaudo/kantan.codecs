package kantan.codecs.laws

import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import kantan.codecs.{DecodeResult, Encoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DoubleCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder = SimpleDecoder(s ⇒ DecodeResult.nonFatalOr(true)(s.toDouble))
  implicit val encoder = Encoder[String, Double](_.toString)

  checkAll("Decoder[String, Double, Boolean]", DecoderTests[String, Double, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Double]", EncoderTests[String, Double].encoder[Int, Int])
}
