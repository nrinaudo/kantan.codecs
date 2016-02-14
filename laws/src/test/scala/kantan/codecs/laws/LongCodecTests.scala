package kantan.codecs.laws

import kantan.codecs.laws.discipline.{EncoderTests, DecoderTests}
import kantan.codecs.{Encoder, DecodeResult, Decoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LongCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val LongDecoder = Decoder[String, Long, Boolean](s ⇒ DecodeResult.nonFatalOr(true)(s.toLong))
  implicit val LongEncoder = Encoder[String, Long](_.toString)

  checkAll("Decoder[String, Long, Boolean]", DecoderTests[String, Long, Boolean].decoder[Int, Int])
  checkAll("Encoder[String, Long]", EncoderTests[String, Long].encoder[Int, Int])
}
