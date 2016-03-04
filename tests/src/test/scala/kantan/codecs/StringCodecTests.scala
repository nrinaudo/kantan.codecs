package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.strings.Codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class StringCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[String]", DecoderTests[String, String, Throwable, Codecs.type].bijectiveDecoder[Int, Int])
  checkAll("StringEncoder[String]", EncoderTests[String, String, Codecs.type].encoder[Int, Int])
  checkAll("StringCodec[String]", CodecTests[String, String, Throwable, Codecs.type].bijectiveCodec[Int, Int])
}


