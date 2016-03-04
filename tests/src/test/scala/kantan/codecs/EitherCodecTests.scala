package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.strings.Codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class EitherCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Either[Int, Boolean]]", DecoderTests[String, Either[Int, Boolean], Throwable, Codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Either[Int, Boolean]]", EncoderTests[String, Either[Int, Boolean], Codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Either[Int, Boolean]]", CodecTests[String, Either[Int, Boolean], Throwable, Codecs.type].codec[Int, Int])
}
