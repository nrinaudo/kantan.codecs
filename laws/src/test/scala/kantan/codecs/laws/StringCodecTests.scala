package kantan.codecs.laws

import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.laws.discipline.{EncoderTests, DecoderTests}
import kantan.codecs.{Encoder, DecodeResult, Decoder}
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class StringCodecTests  extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val intDecoder = Decoder[String, Int, Boolean](s ⇒ DecodeResult(s.toInt).leftMap(_ ⇒ true))
  implicit val intEncoder = Encoder[Int, String](_.toString)

  implicit val arbLegalInt: Arbitrary[LegalValue[String, Int]] = CodecValue.arbLegal(_.toString)
  implicit val arbIllegalInt: Arbitrary[IllegalValue[String]] = CodecValue.arbIllegal(Integer.parseInt)

  checkAll("Decoder[String, Int, Exception]", DecoderTests[String, Int, Boolean].decoder[Int, Int])
  checkAll("Encoder[String, Int, Exception]", EncoderTests[String, Int].encoder[Int, Int])
}


