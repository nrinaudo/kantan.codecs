package kantan.codecs.scalaz

import kantan.codecs.scalaz.laws.discipline._
import kantan.codecs.laws.discipline.{CodecTests => CTests, DecoderTests => DTests, EncoderTests => ETests}
import kantan.codecs.strings.codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

import scalaz.\/

class DisjunctionCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Int \\/ Boolean]", DTests[String, Int \/ Boolean, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Int \\/ Boolean]", ETests[String, Int \/ Boolean, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Int \\/ Boolean]", CTests[String, Int \/ Boolean, Throwable, codecs.type].codec[Int, Int])
}
