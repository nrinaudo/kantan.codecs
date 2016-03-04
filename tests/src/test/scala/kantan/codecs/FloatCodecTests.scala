package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.strings.Codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class FloatCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Float]", DecoderTests[String, Float, Throwable, Codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Float]", EncoderTests[String, Float, Codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Float]", CodecTests[String, Float, Throwable, Codecs.type].codec[Int, Int])
}
