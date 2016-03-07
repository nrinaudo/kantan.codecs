package kantan.codecs.cats

import cats.data.Xor
import kantan.codecs.cats.laws.discipline._
import kantan.codecs.laws.discipline.{CodecTests => CTests, DecoderTests => DTests, EncoderTests => ETests}
import kantan.codecs.strings._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class XorCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Int Xor Boolean]", DTests[String, Int Xor Boolean, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Int Xor Boolean]", ETests[String, Int Xor Boolean, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Int Xor Boolean]", CTests[String, Int Xor Boolean, Throwable, codecs.type].codec[Int, Int])
}
