package kantan.codecs.strings

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tagged._

class ShortCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Short]", DecoderTests[String, Short, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Short]", EncoderTests[String, Short, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Short]", CodecTests[String, Short, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[Short]", DecoderTests[String, Short, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[Short]", EncoderTests[String, Short, tagged.type].encoder[Int, Int])
}
