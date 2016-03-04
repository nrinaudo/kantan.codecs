package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.strings.Codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ByteCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Byte]", DecoderTests[String, Byte, Throwable, Codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Byte]", EncoderTests[String, Byte, Codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Byte]", CodecTests[String, Byte, Throwable, Codecs.type].codec[Int, Int])
}
