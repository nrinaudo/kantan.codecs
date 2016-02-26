package kantan.codecs.laws

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigDecimalCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, BigDecimal, Boolean]", DecoderTests[String, BigDecimal, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, BigDecimal]", EncoderTests[String, BigDecimal, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, BigDecimal]", CodecTests[String, BigDecimal, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
