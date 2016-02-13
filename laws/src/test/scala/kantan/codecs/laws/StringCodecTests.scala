package kantan.codecs.laws

import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import kantan.codecs.{DecodeResult, Decoder, Encoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class StringCodecTests  extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val intDecoder = Decoder[String, Int, Boolean](s ⇒ DecodeResult(s.toInt).leftMap(_ ⇒ true))
  implicit val intEncoder = Encoder[String, Int](_.toString)

  implicit val stringDecoder = Decoder[String, String, Boolean](s ⇒ DecodeResult.success(s))
  implicit val stringEncoder = Encoder[String, String](s ⇒ s)

  checkAll("Decoder[String, Int, Boolean]", DecoderTests[String, Int, Boolean].decoder[Int, Int])
  checkAll("Encoder[String, Int]", EncoderTests[String, Int].encoder[Int, Int])

  checkAll("Decoder[String, String, Boolean]", DecoderTests[String, String, Boolean].bijectiveDecoder[Int, Int])
  checkAll("Encoder[String, String]", EncoderTests[String, String].bijectiveEncoder[Int, Int])
}


