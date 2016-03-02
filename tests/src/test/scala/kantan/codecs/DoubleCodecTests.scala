package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DoubleCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Double, Boolean]", DecoderTests[String, Double, Boolean, Simple].decoder[Int, Int])
  checkAll("Encoder[String, Double]", EncoderTests[String, Double, Simple].encoder[Int, Int])
  checkAll("Codec[String, Double]", CodecTests[String, Double, Boolean, Simple].codec[Int, Int])
}
