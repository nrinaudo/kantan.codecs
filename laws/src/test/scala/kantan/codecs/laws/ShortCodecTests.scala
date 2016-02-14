package kantan.codecs.laws

import kantan.codecs.laws.discipline.{EncoderTests, DecoderTests}
import kantan.codecs.{Encoder, DecodeResult, Decoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ShortCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val ShortDecoder = Decoder[String, Short, Boolean](s ⇒ DecodeResult.nonFatalOr(true)(s.toShort))
  implicit val ShortEncoder = Encoder[String, Short](_.toString)

  checkAll("Decoder[String, Short, Boolean]", DecoderTests[String, Short, Boolean].decoder[Int, Int])
  checkAll("Encoder[String, Short]", EncoderTests[String, Short].encoder[Int, Int])
}
