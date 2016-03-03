package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple.{Codecs, SimpleCodec}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import sun.java2d.pipe.SpanShapeRenderer.Simple

class BigDecimalCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, BigDecimal, Boolean]", DecoderTests[String, BigDecimal, Boolean, Codecs.type].decoder[Int, Int])
  checkAll("Encoder[String, BigDecimal]", EncoderTests[String, BigDecimal, Codecs.type].encoder[Int, Int])
  checkAll("Codec[String, BigDecimal]", CodecTests[String, BigDecimal, Boolean, Codecs.type].codec[Int, Int])
}
