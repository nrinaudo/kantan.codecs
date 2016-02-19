package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigIntCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val codec = SimpleCodec(s ⇒ Result.nonFatalOr(true)(BigInt(s)))(_.toString)

  checkAll("Decoder[String, BigInt, Boolean]", DecoderTests[String, BigInt, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, BigInt]", EncoderTests[String, BigInt, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, BigInt]", CodecTests[String, BigInt, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
