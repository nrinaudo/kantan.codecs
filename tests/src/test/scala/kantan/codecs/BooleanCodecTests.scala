package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.strings.codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BooleanCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Boolean]", DecoderTests[String, Boolean, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Boolean]", EncoderTests[String, Boolean, codecs.type].encoder[Int, Int])
  checkAll("StrnigCodec[Boolean]", CodecTests[String, Boolean, Throwable, codecs.type].codec[Int, Int])
}
