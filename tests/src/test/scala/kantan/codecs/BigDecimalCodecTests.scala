package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple.{Simple, SimpleCodec}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigDecimalCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, BigDecimal, Boolean]", DecoderTests[String, BigDecimal, Boolean, Simple].decoder[Int, Int])
  checkAll("Encoder[String, BigDecimal]", EncoderTests[String, BigDecimal, Simple].encoder[Int, Int])
  checkAll("Codec[String, BigDecimal]", CodecTests[String, BigDecimal, Boolean, Simple].codec[Int, Int])
}
