package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ShortCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val codec = SimpleCodec(s ⇒ Result.nonFatalOr(true)(s.toShort))(_.toString)

  checkAll("Decoder[String, Short, Boolean]", DecoderTests[String, Short, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Short]", EncoderTests[String, Short, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, Short]", CodecTests[String, Short, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
