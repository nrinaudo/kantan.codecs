package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class FloatCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Float, Boolean]", DecoderTests[String, Float, Boolean, Codecs.type].decoder[Int, Int])
  checkAll("Encoder[String, Float]", EncoderTests[String, Float, Codecs.type].encoder[Int, Int])
  checkAll("Codec[String, Float]", CodecTests[String, Float, Boolean, Codecs.type].codec[Int, Int])
}
