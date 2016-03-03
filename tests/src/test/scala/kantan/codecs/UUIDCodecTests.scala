package kantan.codecs

import java.util.UUID

import kantan.codecs.laws.CodecValue._
import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class UUIDCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, UUID, Boolean]", DecoderTests[String, UUID, Boolean, Simple].decoder[Int, Int])
  checkAll("Encoder[String, UUID]", EncoderTests[String, UUID, Simple].encoder[Int, Int])
  checkAll("Codec[String, UUID]", CodecTests[String, UUID, Boolean, Simple].codec[Int, Int])
}