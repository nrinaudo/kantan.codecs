package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LongCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Long, Boolean]", DecoderTests[String, Long, Boolean, Codecs.type].decoder[Int, Int])
  checkAll("Encoder[String, Long]", EncoderTests[String, Long, Codecs.type].encoder[Int, Int])
  checkAll("Codec[String, Long]", CodecTests[String, Long, Boolean, Codecs.type].codec[Int, Int])
}
