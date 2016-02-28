package kantan.codecs

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BooleanCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Boolean, Boolean]", DecoderTests[String, Boolean, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Boolean]", EncoderTests[String, Boolean, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, Boolean]", CodecTests[String, Boolean, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
