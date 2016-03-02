package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class CharCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Char, Boolean]", DecoderTests[String, Char, Boolean, Simple].decoder[Int, Int])
  checkAll("Encoder[String, Char]", EncoderTests[String, Char, Simple].encoder[Int, Int])
  checkAll("Codec[String, Char]", CodecTests[String, Char, Boolean, Simple].codec[Int, Int])
}
