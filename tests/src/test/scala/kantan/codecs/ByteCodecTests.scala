package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import sun.java2d.pipe.SpanShapeRenderer.Simple

class ByteCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Byte, Boolean]", DecoderTests[String, Byte, Boolean, Codecs.type].decoder[Int, Int])
  checkAll("Encoder[String, Byte]", EncoderTests[String, Byte, Codecs.type].encoder[Int, Int])
  checkAll("Codec[String, Byte]", CodecTests[String, Byte, Boolean, Codecs.type].codec[Int, Int])
}
