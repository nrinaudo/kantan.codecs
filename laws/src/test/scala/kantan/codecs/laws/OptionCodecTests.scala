package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class OptionCodecTests  extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val cpdec: SimpleCodec[Option[Int]] =
    SimpleCodec(s ⇒ if(s.isEmpty) Result.success(None) else Result.nonFatalOr(true)(Some(s.toInt)))(_.map(_.toString).getOrElse(""))

  checkAll("Decoder[String, Option[Int], Boolean]", DecoderTests[String, Option[Int], Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Option[Int]]", EncoderTests[String, Option[Int], SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, Option[Int]]", CodecTests[String, Option[Int], Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}