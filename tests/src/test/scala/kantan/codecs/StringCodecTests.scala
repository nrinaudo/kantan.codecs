package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class StringCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, String, Boolean]", DecoderTests[String, String, Boolean, Codecs.type].bijectiveDecoder[Int, Int])
  checkAll("Encoder[String, String]", EncoderTests[String, String, Codecs.type].encoder[Int, Int])
  checkAll("Codec[String, String]", CodecTests[String, String, Boolean, Codecs.type].bijectiveCodec[Int, Int])
}


