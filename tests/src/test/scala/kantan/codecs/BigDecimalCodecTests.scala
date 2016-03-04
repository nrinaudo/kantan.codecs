package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.strings.Codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigDecimalCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[BigDecimal]", DecoderTests[String, BigDecimal, Throwable, Codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[BigDecimal]", EncoderTests[String, BigDecimal, Codecs.type].encoder[Int, Int])
  checkAll("StringCodec[BigDecimal]", CodecTests[String, BigDecimal, Throwable, Codecs.type].codec[Int, Int])
}
