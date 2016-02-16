package kantan.codecs.laws

import kantan.codecs.DecodeResult
import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ByteCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder = SimpleDecoder(s ⇒ DecodeResult.nonFatalOr(true)(s.toByte))
  implicit val encoder = SimpleEncoder[Byte](_.toString)

  checkAll("Decoder[String, Byte, Boolean]", DecoderTests[String, Byte, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Byte]", EncoderTests[String, Byte, SimpleEncoder].encoder[Int, Int])
}
