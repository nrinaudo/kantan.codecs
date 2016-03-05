package kantan.codecs.strings

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tagged._

class StringCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[String]", DecoderTests[String, String, Throwable, codecs.type].bijectiveDecoder[Int, Int])
  checkAll("StringEncoder[String]", EncoderTests[String, String, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[String]", CodecTests[String, String, Throwable, codecs.type].bijectiveCodec[Int, Int])

  checkAll("TaggedDecoder[String]", DecoderTests[String, String, Throwable, tagged.type].bijectiveDecoder[Int, Int])
  checkAll("TaggedEncoder[String]", EncoderTests[String, String, tagged.type].encoder[Int, Int])
}


