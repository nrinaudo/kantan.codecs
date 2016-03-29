package kantan.codecs.scalaz

import kantan.codecs.laws.discipline.{CodecTests => CTests, DecoderTests => DTests, EncoderTests => ETests}
import kantan.codecs.scalaz.laws.discipline.arbitrary._
import kantan.codecs.strings.codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import scalaz.Maybe

class MaybeCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Maybe[Int]]", DTests[String, Maybe[Int], Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Maybe[Int]]", ETests[String, Maybe[Int], codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Maybe[Int]]", CTests[String, Maybe[Int], Throwable, codecs.type].codec[Int, Int])
}
