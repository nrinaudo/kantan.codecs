package kantan.codecs.laws

import java.util.UUID

import kantan.codecs.Result
import kantan.codecs.laws.CodecValue._
import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class UUIDCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val codec = SimpleCodec(s ⇒ Result.nonFatalOr(true)(UUID.fromString(s)))(_.toString)

  checkAll("Decoder[String, UUID, Boolean]", DecoderTests[String, UUID, Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, UUID]", EncoderTests[String, UUID, SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, UUID]", CodecTests[String, UUID, Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
