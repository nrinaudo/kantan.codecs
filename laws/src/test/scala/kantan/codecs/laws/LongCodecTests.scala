package kantan.codecs.laws

import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import kantan.codecs.{DecodeResult, Encoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LongCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder = SimpleDecoder(s ⇒ DecodeResult.nonFatalOr(true)(s.toLong))
  implicit val encoder = Encoder[String, Long](_.toString)

  checkAll("Decoder[String, Long, Boolean]", DecoderTests[String, Long, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Long]", EncoderTests[String, Long].encoder[Int, Int])
}
