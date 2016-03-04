package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.strings.Codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LongCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Long]", DecoderTests[String, Long, Throwable, Codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Long]", EncoderTests[String, Long, Codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Long]", CodecTests[String, Long, Throwable, Codecs.type].codec[Int, Int])
}
