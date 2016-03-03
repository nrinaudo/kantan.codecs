package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import sun.java2d.pipe.SpanShapeRenderer.Simple

class BooleanCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Boolean, Boolean]", DecoderTests[String, Boolean, Boolean, Codecs.type].decoder[Int, Int])
  checkAll("Encoder[String, Boolean]", EncoderTests[String, Boolean, Codecs.type].encoder[Int, Int])
  checkAll("Codec[String, Boolean]", CodecTests[String, Boolean, Boolean, Codecs.type].codec[Int, Int])
}
