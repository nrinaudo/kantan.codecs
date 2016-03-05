package kantan.codecs.strings

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tagged._

class BooleanCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Boolean]", DecoderTests[String, Boolean, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Boolean]", EncoderTests[String, Boolean, codecs.type].encoder[Int, Int])
  checkAll("StrnigCodec[Boolean]", CodecTests[String, Boolean, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[Boolean]", DecoderTests[String, Boolean, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[Boolean]", EncoderTests[String, Boolean, tagged.type].encoder[Int, Int])
}
