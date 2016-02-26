package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class IntCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Int, Boolean]", DecoderTests[String, Int, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Int]", EncoderTests[String, Int, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, Int]", CodecTests[String, Int, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
