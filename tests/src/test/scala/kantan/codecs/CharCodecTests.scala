package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.strings.Codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class CharCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Char]", DecoderTests[String, Char, Throwable, Codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Char]", EncoderTests[String, Char, Codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Char]", CodecTests[String, Char, Throwable, Codecs.type].codec[Int, Int])
}
