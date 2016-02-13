package kantan.codecs.laws

import kantan.codecs.laws.discipline.{EncoderTests, DecoderTests}
import kantan.codecs.{Encoder, DecodeResult, Decoder}
import org.scalacheck.{Gen, Arbitrary}
import org.scalacheck.Gen._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigDecimalCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val BigDecimalDecoder = Decoder[String, BigDecimal, Boolean](s ⇒ DecodeResult(BigDecimal(s)).leftMap(_ ⇒ true))
  implicit val BigDecimalEncoder = Encoder[String, BigDecimal](_.toString)

  checkAll("Decoder[String, BigDecimal, Boolean]", DecoderTests[String, BigDecimal, Boolean].decoder[Int, Int])
  checkAll("Encoder[String, BigDecimal]", EncoderTests[String, BigDecimal].encoder[Int, Int])
}
