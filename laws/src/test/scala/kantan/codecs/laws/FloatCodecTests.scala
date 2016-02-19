package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class FloatCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val codec = SimpleCodec(s ⇒ Result.nonFatalOr(true)(s.toFloat))(_.toString)

  checkAll("Decoder[String, Float, Boolean]", DecoderTests[String, Float, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Float]", EncoderTests[String, Float, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, Float]", CodecTests[String, Float, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
