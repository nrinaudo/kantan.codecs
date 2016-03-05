package kantan.codecs.strings

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tagged._

class ByteCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Byte]", DecoderTests[String, Byte, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Byte]", EncoderTests[String, Byte, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Byte]", CodecTests[String, Byte, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[Byte]", DecoderTests[String, Byte, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[Byte]", EncoderTests[String, Byte, tagged.type].encoder[Int, Int])
}
