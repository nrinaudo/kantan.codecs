package kantan.codecs.strings

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tagged._

class FloatCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Float]", DecoderTests[String, Float, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Float]", EncoderTests[String, Float, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Float]", CodecTests[String, Float, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[Float]", DecoderTests[String, Float, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[Float]", EncoderTests[String, Float, tagged.type].encoder[Int, Int])
}
