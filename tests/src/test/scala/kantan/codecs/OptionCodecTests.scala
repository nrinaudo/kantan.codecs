package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.strings.Codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class OptionCodecTests  extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Option[Int]]", DecoderTests[String, Option[Int], Throwable, Codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Option[Int]]", EncoderTests[String, Option[Int], Codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Option[Int]]", CodecTests[String, Option[Int], Throwable, Codecs.type].codec[Int, Int])
}