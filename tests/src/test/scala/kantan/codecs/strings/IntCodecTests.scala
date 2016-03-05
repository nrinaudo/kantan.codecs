package kantan.codecs.strings

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tagged._

class IntCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Int]", DecoderTests[String, Int, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Int]", EncoderTests[String, Int, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Int]", CodecTests[String, Int, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[Int]", DecoderTests[String, Int, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[Int]", EncoderTests[String, Int, tagged.type].encoder[Int, Int])
}
