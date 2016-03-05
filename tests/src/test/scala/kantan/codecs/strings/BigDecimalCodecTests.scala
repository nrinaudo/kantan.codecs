package kantan.codecs.strings

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tagged._

class BigDecimalCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[BigDecimal]", DecoderTests[String, BigDecimal, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[BigDecimal]", EncoderTests[String, BigDecimal, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[BigDecimal]", CodecTests[String, BigDecimal, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[BigDecimal]", DecoderTests[String, BigDecimal, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[BigDecimal]", EncoderTests[String, BigDecimal, tagged.type].encoder[Int, Int])
}
