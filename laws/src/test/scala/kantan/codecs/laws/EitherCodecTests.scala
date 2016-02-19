package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class EitherCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val codec : SimpleCodec[Either[Int, Boolean]] =
    SimpleCodec(s ⇒ Result.nonFatal(Left(s.toInt)).leftFlatMap(_ ⇒ Result.nonFatal(Right(s.toBoolean): Either[Int, Boolean])).leftMap(_ ⇒ true))
    { _ match {
      case Left(l) ⇒ l.toString
      case Right(r) ⇒ r.toString
    }}

  checkAll("Decoder[String, Either[Int, Boolean], Boolean]", DecoderTests[String, Either[Int, Boolean], Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Either[Int, Boolean]]", EncoderTests[String, Either[Int, Boolean], SimpleEncoder].encoder[Int, Int])
  checkAll("Codec[String, Either[Int, Boolean]]", CodecTests[String, Either[Int, Boolean], Boolean, SimpleDecoder, SimpleEncoder].codec[Int, Int])
}
