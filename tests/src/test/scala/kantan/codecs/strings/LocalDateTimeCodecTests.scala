package kantan.codecs.strings

import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import kantan.codecs.strings.joda.time._
import kantan.codecs.strings.joda.time.laws.discipline.arbitrary._
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LocalDateTimeCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val formatter = DateTimeFormat.mediumDateTime()

  checkAll("StringDecoder[LocalDateTime]",
    DecoderTests[String, LocalDateTime, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[LocalDateTime]", EncoderTests[String, LocalDateTime, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[LocalDateTime]", CodecTests[String, LocalDateTime, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[LocalDateTime]",
    DecoderTests[String, LocalDateTime, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[LocalDateTime]", EncoderTests[String, LocalDateTime, tagged.type].encoder[Int, Int])
}
