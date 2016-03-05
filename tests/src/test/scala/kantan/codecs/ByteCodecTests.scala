package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.strings.codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ByteCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Byte]", DecoderTests[String, Byte, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Byte]", EncoderTests[String, Byte, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Byte]", CodecTests[String, Byte, Throwable, codecs.type].codec[Int, Int])
}
