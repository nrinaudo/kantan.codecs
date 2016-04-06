package kantan.codecs.strings

import java.net.URL
import kantan.codecs.laws.CodecValue._
import kantan.codecs.laws.discipline._
import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.strings.tagged._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class URLCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[URL]", DecoderTests[String, URL, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[URL]", EncoderTests[String, URL, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[URL]", CodecTests[String, URL, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[URL]", DecoderTests[String, URL, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[URL]", EncoderTests[String, URL, tagged.type].encoder[Int, Int])
}
