package kantan.codecs.strings

import java.net.URI
import kantan.codecs.laws.CodecValue._
import kantan.codecs.laws.discipline._
import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.strings.tagged._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class URICodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[URI]", DecoderTests[String, URI, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[URI]", EncoderTests[String, URI, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[URI]", CodecTests[String, URI, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[URI]", DecoderTests[String, URI, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[URI]", EncoderTests[String, URI, tagged.type].encoder[Int, Int])
}
