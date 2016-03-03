package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import sun.java2d.pipe.SpanShapeRenderer.Simple

class BigIntCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, BigInt, Boolean]", DecoderTests[String, BigInt, Boolean, Codecs.type].decoder[Int, Int])
  checkAll("Encoder[String, BigInt]", EncoderTests[String, BigInt, Codecs.type].encoder[Int, Int])
  checkAll("Codec[String, BigInt]", CodecTests[String, BigInt, Boolean, Codecs.type].codec[Int, Int])
}
