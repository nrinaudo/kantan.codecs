package kantan.codecs.laws

import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import kantan.codecs.{DecodeResult, Encoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigIntCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val BigIntDecoder = SimpleDecoder(s ⇒ DecodeResult.nonFatalOr(true)(BigInt(s)))
  implicit val BigIntEncoder = Encoder[String, BigInt](_.toString)

  checkAll("Decoder[String, BigInt, Boolean]", DecoderTests[String, BigInt, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, BigInt]", EncoderTests[String, BigInt].encoder[Int, Int])
}
