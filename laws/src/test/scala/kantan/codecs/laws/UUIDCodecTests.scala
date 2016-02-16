package kantan.codecs.laws

import java.util.UUID

import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import kantan.codecs.{DecodeResult, Encoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class UUIDCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder = SimpleDecoder(s ⇒ DecodeResult.nonFatalOr(true)(UUID.fromString(s)))
  implicit val encoder = Encoder[String, UUID](_.toString)

  checkAll("Decoder[String, UUID, Boolean]", DecoderTests[String, UUID, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, UUID]", EncoderTests[String, UUID].encoder[Int, Int])
}
