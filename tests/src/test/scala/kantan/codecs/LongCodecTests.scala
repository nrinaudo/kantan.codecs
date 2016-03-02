package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LongCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Long, Boolean]", DecoderTests[String, Long, Boolean, Simple].decoder[Int, Int])
  checkAll("Encoder[String, Long]", EncoderTests[String, Long, Simple].encoder[Int, Int])
  checkAll("Codec[String, Long]", CodecTests[String, Long, Boolean, Simple].codec[Int, Int])
}
