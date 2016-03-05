package kantan.codecs.strings

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tagged._

class BigIntCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[BigInt]", DecoderTests[String, BigInt, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[BigInt]", EncoderTests[String, BigInt, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[BigInt]", CodecTests[String, BigInt, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[BigInt]", DecoderTests[String, BigInt, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[BigInt]", EncoderTests[String, BigInt, tagged.type].encoder[Int, Int])
}
