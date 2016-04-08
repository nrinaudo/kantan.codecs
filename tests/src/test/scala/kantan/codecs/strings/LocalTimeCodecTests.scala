package kantan.codecs.strings

import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import kantan.codecs.strings.joda.time._
import kantan.codecs.strings.joda.time.laws.discipline.arbitrary._
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LocalTimeCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val formatter = DateTimeFormat.mediumTime()

  checkAll("StringDecoder[LocalTime]", DecoderTests[String, LocalTime, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[LocalTime]", EncoderTests[String, LocalTime, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[LocalTime]", CodecTests[String, LocalTime, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[LocalTime]", DecoderTests[String, LocalTime, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[LocalTime]", EncoderTests[String, LocalTime, tagged.type].encoder[Int, Int])
}
