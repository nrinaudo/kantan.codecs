package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigDecimalCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val codec = SimpleCodec(s ⇒ Result.nonFatalOr(true)(BigDecimal(s)))(_.toString)

  checkAll("Decoder[String, BigDecimal, Boolean]", DecoderTests[String, BigDecimal, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, BigDecimal]", EncoderTests[String, BigDecimal, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, BigDecimal]", CodecTests[String, BigDecimal, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
