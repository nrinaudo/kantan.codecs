package kantan.codecs.laws

import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import kantan.codecs.{DecodeResult, Encoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class IntCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder = SimpleDecoder(s ⇒ DecodeResult.nonFatalOr(true)(s.toInt))
  implicit val encoder = Encoder[String, Int](_.toString)

  checkAll("Decoder[String, Int, Boolean]", DecoderTests[String, Int, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Int]", EncoderTests[String, Int].encoder[Int, Int])
}
