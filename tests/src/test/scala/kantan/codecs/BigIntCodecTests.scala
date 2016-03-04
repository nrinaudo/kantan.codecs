package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.strings.Codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigIntCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[BigInt]", DecoderTests[String, BigInt, Throwable, Codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[BigInt]", EncoderTests[String, BigInt, Codecs.type].encoder[Int, Int])
  checkAll("StringCodec[BigInt]", CodecTests[String, BigInt, Throwable, Codecs.type].codec[Int, Int])
}
