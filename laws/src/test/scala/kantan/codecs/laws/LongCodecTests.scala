package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LongCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Long, Boolean]", DecoderTests[String, Long, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Long]", EncoderTests[String, Long, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, Long]", CodecTests[String, Long, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
