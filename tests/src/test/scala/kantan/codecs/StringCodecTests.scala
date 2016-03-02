package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class StringCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, String, Boolean]", DecoderTests[String, String, Boolean, Simple].bijectiveDecoder[Int, Int])
  checkAll("Encoder[String, String]", EncoderTests[String, String, Simple].encoder[Int, Int])
  checkAll("Codec[String, String]", CodecTests[String, String, Boolean, Simple].bijectiveCodec[Int, Int])
}


