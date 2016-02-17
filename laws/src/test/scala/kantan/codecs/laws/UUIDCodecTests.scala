package kantan.codecs.laws

import java.util.UUID

import kantan.codecs.Result
import kantan.codecs.laws.discipline.{DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class UUIDCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(UUID.fromString(s)))
  implicit val encoder = SimpleEncoder[UUID](_.toString)

  checkAll("Decoder[String, UUID, Boolean]", DecoderTests[String, UUID, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, UUID]", EncoderTests[String, UUID, SimpleEncoder].encoder[Int, Int])
}
