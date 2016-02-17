package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class CharCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder = SimpleDecoder { s ⇒
    if(s.length == 1) Result.success(s.charAt(0))
    else              Result.failure(true)
  }
  implicit val encoder = SimpleEncoder[Char](_.toString)

  checkAll("Decoder[String, Char, Boolean]", DecoderTests[String, Char, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Char]", EncoderTests[String, Char, SimpleEncoder].encoder[Int, Int])
}
