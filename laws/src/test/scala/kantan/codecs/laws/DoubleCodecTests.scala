package kantan.codecs.laws

import kantan.codecs.laws.discipline.{EncoderTests, DecoderTests}
import kantan.codecs.{Encoder, DecodeResult, Decoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DoubleCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val DoubleDecoder = Decoder[String, Double, Boolean](s ⇒ DecodeResult.nonFatalOr(true)(s.toDouble))
  implicit val DoubleEncoder = Encoder[String, Double](_.toString)

  checkAll("Decoder[String, Double, Boolean]", DecoderTests[String, Double, Boolean].decoder[Int, Int])
  checkAll("Encoder[String, Double]", EncoderTests[String, Double].encoder[Int, Int])
}
