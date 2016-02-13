package kantan.codecs.laws

import kantan.codecs.laws.discipline.{EncoderTests, DecoderTests}
import kantan.codecs.{Encoder, DecodeResult, Decoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class CharCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val CharDecoder = Decoder[String, Char, Boolean] { s ⇒
    if(s.length == 1) DecodeResult.success(s.charAt(0))
    else              DecodeResult.failure(true)
  }
  implicit val CharEncoder = Encoder[String, Char](_.toString)

  checkAll("Decoder[String, Char, Boolean]", DecoderTests[String, Char, Boolean].decoder[Int, Int])
  checkAll("Encoder[String, Char]", EncoderTests[String, Char].encoder[Int, Int])
}
