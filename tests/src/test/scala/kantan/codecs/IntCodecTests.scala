package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class IntCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Int, Boolean]", DecoderTests[String, Int, Boolean, Codecs.type].decoder[Int, Int])
  checkAll("Encoder[String, Int]", EncoderTests[String, Int, Codecs.type].encoder[Int, Int])
  checkAll("Codec[String, Int]", CodecTests[String, Int, Boolean, Codecs.type].codec[Int, Int])
}
