package kantan.codecs.laws

import kantan.codecs.laws.discipline.{EncoderTests, DecoderTests}
import kantan.codecs.{Encoder, DecodeResult, Decoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class IntCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val intDecoder = Decoder[String, Int, Boolean](s ⇒ DecodeResult(s.toInt).leftMap(_ ⇒ true))
  implicit val intEncoder = Encoder[String, Int](_.toString)

  checkAll("Decoder[String, Int, Boolean]", DecoderTests[String, Int, Boolean].decoder[Int, Int])
  checkAll("Encoder[String, Int]", EncoderTests[String, Int].encoder[Int, Int])
}
