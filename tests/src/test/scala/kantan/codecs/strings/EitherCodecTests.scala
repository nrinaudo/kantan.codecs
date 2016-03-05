package kantan.codecs.strings

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tagged._

class EitherCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Either[Int, Boolean]]", DecoderTests[String, Either[Int, Boolean], Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Either[Int, Boolean]]", EncoderTests[String, Either[Int, Boolean], codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Either[Int, Boolean]]", CodecTests[String, Either[Int, Boolean], Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[Either[Int, Boolean]]", DecoderTests[String, Either[Int, Boolean], Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[Either[Int, Boolean]]", EncoderTests[String, Either[Int, Boolean], tagged.type].encoder[Int, Int])
}
