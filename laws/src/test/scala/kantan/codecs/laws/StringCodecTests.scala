package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class StringCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, String, Boolean]", DecoderTests[String, String, Boolean, SimpleDecoder].bijectiveDecoder[Int, Int])
  checkAll("Encoder[String, String]", EncoderTests[String, String, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, String]", CodecTests[String, String, Boolean, SimpleDecoder, SimpleEncoder].bijectiveCodec[Int, Int])
}


