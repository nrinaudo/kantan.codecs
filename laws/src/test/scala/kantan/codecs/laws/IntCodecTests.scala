package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class IntCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(s.toInt))
  implicit val encoder = SimpleEncoder[Int](_.toString)

  checkAll("Decoder[String, Int, Boolean]", DecoderTests[String, Int, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Int]", EncoderTests[String, Int, SimpleEncoder].encoder[Int, Int])
}
