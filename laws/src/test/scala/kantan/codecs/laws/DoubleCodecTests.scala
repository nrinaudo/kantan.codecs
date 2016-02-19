package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DoubleCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val codec = SimpleCodec(s ⇒ Result.nonFatalOr(true)(s.toDouble))(_.toString)

  checkAll("Decoder[String, Double, Boolean]", DecoderTests[String, Double, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Double]", EncoderTests[String, Double, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, Double]", CodecTests[String, Double, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
