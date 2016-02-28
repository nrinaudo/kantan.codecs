package kantan.codecs

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DoubleCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Double, Boolean]", DecoderTests[String, Double, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Double]", EncoderTests[String, Double, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, Double]", CodecTests[String, Double, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
