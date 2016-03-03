package kantan.codecs

import kantan.codecs.laws.discipline._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class EitherCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Decoder[String, Either[Int, Boolean], Boolean]", DecoderTests[String, Either[Int, Boolean], Boolean, Codecs.type].decoder[Int, Int])
  checkAll("Encoder[String, Either[Int, Boolean]]", EncoderTests[String, Either[Int, Boolean], Codecs.type].encoder[Int, Int])
  checkAll("Codec[String, Either[Int, Boolean]]", CodecTests[String, Either[Int, Boolean], Boolean, Codecs.type].codec[Int, Int])
}
