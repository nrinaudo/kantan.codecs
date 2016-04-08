package kantan.codecs.strings

import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import kantan.codecs.strings.joda.time._
import kantan.codecs.strings.joda.time.laws.discipline.arbitrary._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DateTimeCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val formatter = ISODateTimeFormat.dateTime

  checkAll("StringDecoder[DateTime]", DecoderTests[String, DateTime, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[DateTime]", EncoderTests[String, DateTime, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[DateTime]", CodecTests[String, DateTime, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[DateTime]", DecoderTests[String, DateTime, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[DateTime]", EncoderTests[String, DateTime, tagged.type].encoder[Int, Int])
}
