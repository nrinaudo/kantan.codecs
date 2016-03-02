package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ShortCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Short, Boolean]", DecoderTests[String, Short, Boolean, Simple].decoder[Int, Int])
  checkAll("Encoder[String, Short]", EncoderTests[String, Short, Simple].encoder[Int, Int])
  checkAll("Codec[String, Short]", CodecTests[String, Short, Boolean, Simple].codec[Int, Int])
}
