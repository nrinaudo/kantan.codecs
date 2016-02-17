package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ShortCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(s.toShort))
  implicit val encoder = SimpleEncoder[Short](_.toString)

  checkAll("Decoder[String, Short, Boolean]", DecoderTests[String, Short, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Short]", EncoderTests[String, Short, SimpleEncoder].encoder[Int, Int])
}
