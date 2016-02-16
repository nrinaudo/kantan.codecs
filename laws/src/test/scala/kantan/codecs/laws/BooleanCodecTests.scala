package kantan.codecs.laws

import kantan.codecs.DecodeResult
import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BooleanCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder = SimpleDecoder(s ⇒ DecodeResult.nonFatalOr(true)(s.toBoolean))
  implicit val encoder = SimpleEncoder[Boolean](_.toString)

  checkAll("Decoder[String, Boolean, Boolean]", DecoderTests[String, Boolean, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Boolean]", EncoderTests[String, Boolean, SimpleEncoder].encoder[Int, Int])
}
