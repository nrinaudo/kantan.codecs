package kantan.codecs.strings

import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import kantan.codecs.strings.joda.time._
import kantan.codecs.strings.joda.time.laws.discipline.arbitrary._
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LocalDateCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val formatter = DateTimeFormat.mediumDate()

  checkAll("StringDecoder[LocalDate]",DecoderTests[String, LocalDate, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[LocalDate]", EncoderTests[String, LocalDate, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[LocalDate]", CodecTests[String, LocalDate, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[LocalDate]", DecoderTests[String, LocalDate, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[LocalDate]", EncoderTests[String, LocalDate, tagged.type].encoder[Int, Int])
}
