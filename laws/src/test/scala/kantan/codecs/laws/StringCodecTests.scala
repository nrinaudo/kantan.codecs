package kantan.codecs.laws

import kantan.codecs.DecodeResult
import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class StringCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder = SimpleDecoder(s ⇒ DecodeResult.success(s))
  implicit val encoder = SimpleEncoder[String](s ⇒ s)

  checkAll("Decoder[String, String, Boolean]", DecoderTests[String, String, Boolean, SimpleDecoder].bijectiveDecoder[Int, Int])
  checkAll("Encoder[String, String]", EncoderTests[String, String, SimpleEncoder].bijectiveEncoder[Int, Int])
}


