package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.strings.Codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DoubleCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Double]", DecoderTests[String, Double, Throwable, Codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Double]", EncoderTests[String, Double, Codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Double]", CodecTests[String, Double, Throwable, Codecs.type].codec[Int, Int])
}
