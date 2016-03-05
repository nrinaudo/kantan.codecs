package kantan.codecs.strings

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tagged._

class DoubleCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Double]", DecoderTests[String, Double, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Double]", EncoderTests[String, Double, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Double]", CodecTests[String, Double, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[Double]", DecoderTests[String, Double, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[Double]", EncoderTests[String, Double, tagged.type].encoder[Int, Int])
}
