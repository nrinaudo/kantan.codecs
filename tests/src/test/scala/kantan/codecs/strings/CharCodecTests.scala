package kantan.codecs.strings

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tagged._

class CharCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Char]", DecoderTests[String, Char, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Char]", EncoderTests[String, Char, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Char]", CodecTests[String, Char, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[Char]", DecoderTests[String, Char, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[Char]", EncoderTests[String, Char, tagged.type].encoder[Int, Int])
}
