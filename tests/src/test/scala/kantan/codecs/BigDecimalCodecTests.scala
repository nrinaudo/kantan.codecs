package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.strings.codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigDecimalCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[BigDecimal]", DecoderTests[String, BigDecimal, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[BigDecimal]", EncoderTests[String, BigDecimal, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[BigDecimal]", CodecTests[String, BigDecimal, Throwable, codecs.type].codec[Int, Int])
}
