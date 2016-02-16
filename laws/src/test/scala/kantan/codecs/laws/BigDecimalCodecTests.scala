package kantan.codecs.laws

import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import kantan.codecs.{DecodeResult, Encoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigDecimalCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder = SimpleDecoder(s ⇒ DecodeResult.nonFatalOr(true)(BigDecimal(s)))
  implicit val encoder = Encoder[String, BigDecimal](_.toString)

  checkAll("Decoder[String, BigDecimal, Boolean]", DecoderTests[String, BigDecimal, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, BigDecimal]", EncoderTests[String, BigDecimal].encoder[Int, Int])
}
