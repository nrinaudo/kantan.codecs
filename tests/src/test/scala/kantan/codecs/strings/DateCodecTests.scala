package kantan.codecs.strings

import java.text.SimpleDateFormat
import java.util.{Date, Locale}
import kantan.codecs.laws.discipline._
import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.strings.tagged._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DateCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH)
  //formatter.setTimeZone(TimeZone.getTimeZone("UTC"))

  implicit val strDate: GenCodecValue[String, Date] =
    GenCodecValue.nonFatal[String, Date](formatter.format)(formatter.parse)

  checkAll("StringDecoder[Date]", DecoderTests[String, Date, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Date]", EncoderTests[String, Date, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Date]", CodecTests[String, Date, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[Date]", DecoderTests[String, Date, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[Date]", EncoderTests[String, Date, tagged.type].encoder[Int, Int])
}

