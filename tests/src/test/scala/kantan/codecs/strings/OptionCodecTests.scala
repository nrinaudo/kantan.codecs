package kantan.codecs.strings

import kantan.codecs.laws.discipline._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tagged._

class OptionCodecTests  extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Option[Int]]", DecoderTests[String, Option[Int], Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Option[Int]]", EncoderTests[String, Option[Int], codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Option[Int]]", CodecTests[String, Option[Int], Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[Option[Int]]", DecoderTests[String, Option[Int], Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[Option[Int]]", EncoderTests[String, Option[Int], tagged.type].encoder[Int, Int])
}