package kantan.codecs

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigIntCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, BigInt, Boolean]", DecoderTests[String, BigInt, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, BigInt]", EncoderTests[String, BigInt, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, BigInt]", CodecTests[String, BigInt, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
