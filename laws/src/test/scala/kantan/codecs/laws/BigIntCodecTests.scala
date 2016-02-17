package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigIntCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(BigInt(s)))
  implicit val encoder = SimpleEncoder[BigInt](_.toString)

  checkAll("Decoder[String, BigInt, Boolean]", DecoderTests[String, BigInt, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, BigInt]", EncoderTests[String, BigInt, SimpleEncoder].encoder[Int, Int])
}
