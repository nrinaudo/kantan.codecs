package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LongCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val codec = SimpleCodec(s ⇒ Result.nonFatalOr(true)(s.toLong))(_.toString)

  checkAll("Decoder[String, Long, Boolean]", DecoderTests[String, Long, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Long]", EncoderTests[String, Long, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, Long]", CodecTests[String, Long, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
