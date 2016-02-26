package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ByteCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Byte, Boolean]", DecoderTests[String, Byte, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Byte]", EncoderTests[String, Byte, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, Byte]", CodecTests[String, Byte, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
