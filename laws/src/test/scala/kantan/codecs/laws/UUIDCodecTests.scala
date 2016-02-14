package kantan.codecs.laws

import java.util.UUID

import kantan.codecs.laws.discipline.{EncoderTests, DecoderTests}
import kantan.codecs.{Encoder, DecodeResult, Decoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class UUIDCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val UUIDDecoder = Decoder[String, UUID, Boolean](s ⇒ DecodeResult.nonFatalOr(true)(UUID.fromString(s)))
  implicit val UUIDEncoder = Encoder[String, UUID](_.toString)

  checkAll("Decoder[String, UUID, Boolean]", DecoderTests[String, UUID, Boolean].decoder[Int, Int])
  checkAll("Encoder[String, UUID]", EncoderTests[String, UUID].encoder[Int, Int])
}
