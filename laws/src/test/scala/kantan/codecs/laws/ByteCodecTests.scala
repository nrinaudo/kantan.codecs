package kantan.codecs.laws

import kantan.codecs.laws.discipline.{EncoderTests, DecoderTests}
import kantan.codecs.{Encoder, DecodeResult, Decoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ByteCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val ByteDecoder = Decoder[String, Byte, Boolean](s ⇒ DecodeResult.nonFatalOr(true)(s.toByte))
  implicit val ByteEncoder = Encoder[String, Byte](_.toString)

  checkAll("Decoder[String, Byte, Boolean]", DecoderTests[String, Byte, Boolean].decoder[Int, Int])
  checkAll("Encoder[String, Byte]", EncoderTests[String, Byte].encoder[Int, Int])
}
