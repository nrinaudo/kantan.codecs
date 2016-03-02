package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BooleanCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Boolean, Boolean]", DecoderTests[String, Boolean, Boolean, Simple].decoder[Int, Int])
  checkAll("Encoder[String, Boolean]", EncoderTests[String, Boolean, Simple].encoder[Int, Int])
  checkAll("Codec[String, Boolean]", CodecTests[String, Boolean, Boolean, Simple].codec[Int, Int])
}
