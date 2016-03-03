package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import sun.java2d.pipe.SpanShapeRenderer.Simple

class CharCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Char, Boolean]", DecoderTests[String, Char, Boolean, Codecs.type].decoder[Int, Int])
  checkAll("Encoder[String, Char]", EncoderTests[String, Char, Codecs.type].encoder[Int, Int])
  checkAll("Codec[String, Char]", CodecTests[String, Char, Boolean, Codecs.type].codec[Int, Int])
}
