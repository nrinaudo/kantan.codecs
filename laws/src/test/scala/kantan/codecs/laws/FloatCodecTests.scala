package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class FloatCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Float, Boolean]", DecoderTests[String, Float, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Float]", EncoderTests[String, Float, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, Float]", CodecTests[String, Float, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
